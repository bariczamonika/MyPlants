package ie.dbs.myplants;

class WeatherInfo {
    private double currentTemp;
    private double currentMinTemp;
    private double currentMaxTemp;
    private double currentWindSpeed;
    private String weatherIcon;
    private String dateTime;
    private String briefDescription;
    private String detailedDescription;
    WeatherInfo() {

    }

    public WeatherInfo(double currentTemp, double currentMinTemp, double currentMaxTemp, double currentWindSpeed) {
        this.currentTemp = currentTemp;
        this.currentMinTemp = currentMinTemp;
        this.currentMaxTemp = currentMaxTemp;
        this.currentWindSpeed = currentWindSpeed;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    double getCurrentTemp() {
        return currentTemp;
    }

    void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
    }

    double getCurrentMinTemp() {
        return currentMinTemp;
    }

    void setCurrentMinTemp(double currentMinTemp) {
        this.currentMinTemp = currentMinTemp;
    }

    double getCurrentMaxTemp() {
        return currentMaxTemp;
    }

    void setCurrentMaxTemp(double currentMaxTemp) {
        this.currentMaxTemp = currentMaxTemp;
    }

    double getCurrentWindSpeed() {
        return currentWindSpeed;
    }

    void setCurrentWindSpeed(double currentWindSpeed) {
        this.currentWindSpeed = currentWindSpeed;
    }

}
