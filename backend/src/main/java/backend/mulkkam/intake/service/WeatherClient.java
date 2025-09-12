package backend.mulkkam.intake.service;

import backend.mulkkam.intake.dto.OpenWeatherResponse;

public interface WeatherClient {

    OpenWeatherResponse getFourDayWeatherForecast(String cityCode);
}
