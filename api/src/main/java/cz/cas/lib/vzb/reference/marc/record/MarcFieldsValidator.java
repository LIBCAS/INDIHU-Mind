package cz.cas.lib.vzb.reference.marc.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.exception.BadArgument;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static core.exception.BadArgument.ErrorCode.WRONG_MARC_FORMAT;

@Service
@Slf4j
public class MarcFieldsValidator {

    public static final String MARC_FIELDS_FILE_PATH = "marc-fields.json";

    private ObjectMapper objectMapper;

    /**
     * Key: Tag {@link MarcFieldStructure#tag}
     */
    private final Map<String, List<MarcFieldStructure>> marcFieldsMap = new HashMap<>();

    @PostConstruct
    private void initMarcFields() throws IOException {
        MarcFieldStructure[] marcFields = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(MARC_FIELDS_FILE_PATH), MarcFieldStructure[].class);
        for (MarcFieldStructure field : marcFields) {
            marcFieldsMap.computeIfAbsent(field.getTag(), key -> new ArrayList<>()).add(field);
        }
        log.info("Marc fields were initialized from file: " + MARC_FIELDS_FILE_PATH);
    }


    /**
     * Validates Datafields from a request.
     *
     * Compares them to the supported fields declared in a file in application resources.
     *
     * @param dtoFields {@link Datafield}s from a request
     * @throws BadArgument if validation fails
     */
    public void validate(List<Datafield> dtoFields) {
        for (Datafield field : dtoFields) {
            List<MarcFieldStructure> fieldsForTag = marcFieldsMap.get(field.getTag());
            assertFieldHasSupportedTag(field, fieldsForTag);

            List<Character> supportedCodes = fieldsForTag.stream().map(MarcFieldStructure::getCode).collect(Collectors.toList());
            List<Character> codesOfField = field.getSubfields().stream().map(Subfield::getCode).collect(Collectors.toList());

            assertFieldContainsSupportedCodes(field, supportedCodes, codesOfField);
            assertFieldHasNoDuplicatedCodes(field, codesOfField);
        }
    }

    private void assertFieldHasSupportedTag(Datafield field, List<MarcFieldStructure> fieldsForTag) {
        if (fieldsForTag == null)
            throw new BadArgument(WRONG_MARC_FORMAT, "Datafield:{" + field + "} has a tag that is not supported");
    }

    private void assertFieldContainsSupportedCodes(Datafield field, List<Character> supportedCodes, List<Character> codesOfField) {
        for (Character character : codesOfField) {
            if (!supportedCodes.contains(character))
                throw new BadArgument(WRONG_MARC_FORMAT, "Datafield:{" + field + "} contains code that is not supported: '" + character + "'");
        }
    }

    private void assertFieldHasNoDuplicatedCodes(Datafield field, List<Character> codesOfField) {
        if (!codesOfField.stream().sequential().allMatch(new HashSet<>()::add)) {
            throw new BadArgument(WRONG_MARC_FORMAT, "Datafield:{" + field + "} contains code that occurs more than once.");
        }
    }


    @Inject
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Data
    private static class MarcFieldStructure {
        private String czech;
        private String tag;
        private char code;
    }
}

