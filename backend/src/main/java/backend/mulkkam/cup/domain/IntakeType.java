package backend.mulkkam.cup.domain;

import backend.mulkkam.common.exception.CommonException;
import java.util.Arrays;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_TYPE;

public enum IntakeType {
    WATER,
    COFFEE,
    ;

    public static IntakeType findByName(String name) {
        return Arrays.stream(values())
                .filter(intakeType -> intakeType.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_TYPE));
    }
}
