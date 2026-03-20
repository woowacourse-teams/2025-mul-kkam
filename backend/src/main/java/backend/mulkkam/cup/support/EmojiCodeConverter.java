package backend.mulkkam.cup.support;

import backend.mulkkam.cup.domain.EmojiCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EmojiCodeConverter implements AttributeConverter<EmojiCode, String> {

    @Override
    public String convertToDatabaseColumn(EmojiCode attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public EmojiCode convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return EmojiCode.of(dbData);
    }
}
