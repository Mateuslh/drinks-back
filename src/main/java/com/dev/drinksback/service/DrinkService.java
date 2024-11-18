package com.dev.drinksback.service;

import com.dev.drinksback.exception.DrinkGenerationException;
import com.dev.drinksback.exception.EntidadeNaoEncontradaException;
import com.dev.drinksback.model.Admin;
import com.dev.drinksback.model.Drink;
import com.dev.drinksback.repository.DrinkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.flashvayne.chatgpt.dto.image.ImageFormat;
import io.github.flashvayne.chatgpt.dto.image.ImageSize;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DrinkService {

    private static final String ALCOHOLIC_TEXT = "contém álcool";
    private static final String NON_ALCOHOLIC_TEXT = "não contém álcool";

    private static final String PROMPT_TEMPLATE = """
            Me gere um drink com essa descrição "%s", que contenha %s e seja do tipo %s. Caso essa descrição não seja de um drink valido, inserir na propriedade isValid false, e retornar as outras propriedades null. Por favor, retorne em UTF-8, Português, no seguinte formato:
            {
               "isValid": true,
               "briefDescription": "descrição visual do drink. Ex: drink de limão com suor na taça e cores alaranjadas nas bordas, com um limão fatiado no topo",
               "drinkName": "nome do drink gerado",
               "ingredients": [
                  { "name": "Limão", "quantity": "1 unidade" },
                  { "name": "Cachaça", "quantity": "50 ml" }
               ],
               "preparationMode": [
                  {
                     "step": 1,
                     "title": "misturar ingredientes",
                     "description": "pegar x coisa e misturar com 50 ml de ingrediente 1"
                  },
                  {
                     "step": 2,
                     "title": "bater com gelo",
                     "description": "bater com gelo os ingredientes misturados no passo 1"
                  }
               ]
            }
            """;

    private final ChatgptService chatgptService;
    private final ObjectMapper objectMapper;
    private final DrinkRepository drinkRepository;

    @Value("${drink.generation.maxRetries:3}")
    private int maxRetries;

    @Value("${drink.generation.imageSize:LARGE}")
    private ImageSize imageSize;

    public Drink generateDrink(String category, Boolean isAlcoholic, Boolean needPhoto, String description, Admin admin) {
        validateInput(category, isAlcoholic, description);

        String alcoholContent = isAlcoholic ? ALCOHOLIC_TEXT : NON_ALCOHOLIC_TEXT;
        String prompt = String.format(PROMPT_TEMPLATE, description, alcoholContent, category);

        Drink drink = fetchDrinkFromChatGPTWithRetries(prompt);

        if (Boolean.TRUE.equals(needPhoto)) {
            String imageBase64 = generateDrinkImage(drink.getBriefDescription());
            drink.setImageBase64(imageBase64);
        }

        drink.setAdmin(admin);
        return saveDrink(drink);
    }

    private Drink fetchDrinkFromChatGPTWithRetries(String prompt) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                attempt++;
                log.info("Tentativa {} de {} para gerar o drink.", attempt, maxRetries);
                String drinkJson = chatgptService.sendMessage(prompt);
                Drink drink = parseDrinkFromJson(drinkJson);

                if (Boolean.FALSE.equals(drink.isValid())) {
                    log.warn("Resposta inválida recebida na tentativa {}.", attempt);
                    if (attempt >= maxRetries) {
                        throw new IllegalArgumentException("O prompt fornecido não é válido.");
                    }
                    continue; // Tentar novamente
                }

                return drink;
            } catch (Exception e) {
                log.error("Erro na tentativa {}: {}", attempt, e.getMessage());
                if (attempt >= maxRetries) {
                    log.error("Falha ao gerar informações do drink após {} tentativas.", maxRetries);
                    throw new DrinkGenerationException("Falha ao gerar informações do drink após várias tentativas", e);
                }
            }
        }
        throw new DrinkGenerationException("Falha inesperada ao gerar o drink");
    }

    private Drink parseDrinkFromJson(String drinkJson) {
        try {
            return objectMapper.readValue(drinkJson, Drink.class);
        } catch (Exception e) {
            log.error("Falha ao parsear informações do drink: {}", e.getMessage(), e);
            throw new DrinkGenerationException("Falha ao parsear informações do drink", e);
        }
    }

    private String generateDrinkImage(String briefDescription) {
        String imagePrompt = String.format("Crie uma imagem do drink: %s.", briefDescription);
        try {
            return chatgptService.imageGenerate(imagePrompt, 1, imageSize, ImageFormat.BASE64).get(0);
        } catch (Exception e) {
            log.error("Falha ao gerar imagem do drink: {}", e.getMessage(), e);
            throw new DrinkGenerationException("Falha ao gerar imagem do drink", e);
        }
    }

    private void validateInput(String category, Boolean isAlcoholic, String description) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("A categoria não pode ser vazia");
        }
        if (isAlcoholic == null) {
            throw new IllegalArgumentException("O indicador de teor alcoólico não pode ser nulo");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode ser vazia");
        }
    }

    public Page<Drink> findAll(Pageable pageable, Admin admin) {
        return drinkRepository.findByAdminId(admin.getId(), pageable);
    }

    public Page<Drink> getDrinksByAdminIdAndDrinkName(Admin admin, String drinkName, Pageable pageable) {
        return drinkRepository.findByAdminIdAndDrinkNameContaining(admin.getId(), drinkName, pageable);
    }

    public Page<Drink> findByAdminIdAndDrinkNameSimilar(Admin admin, String drinkName, Pageable pageable) {
        return drinkRepository.findByAdminIdAndDrinkNameSimilar(admin.getId(), drinkName, pageable);
    }

    @Transactional
    public Drink saveDrink(Drink drink) {
        setBidirectionalAssociations(drink);
        try {
            return drinkRepository.save(drink);
        } catch (DataAccessException e) {
            log.error("Falha ao salvar o drink: {}", e.getMessage(), e);
            throw new DrinkGenerationException("Falha ao salvar o drink", e);
        }
    }

    private void setBidirectionalAssociations(Drink drink) {
        if (drink.getIngredients() != null) {
            drink.getIngredients().forEach(ingredient -> ingredient.setDrink(drink));
        }

        if (drink.getPreparationMode() != null) {
            drink.getPreparationMode().forEach(step -> step.setDrink(drink));
        }
    }

    public Drink findByIdAndAdmin(Long id, Admin admin) {
        return drinkRepository.findByAdminIdAndId(admin.getId(), id).orElseThrow(() -> new EntidadeNaoEncontradaException(Drink.class));
    }
}
