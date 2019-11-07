package ie.dbs.myplants;

class WeatherInfo {
    private double currentTemp;
    private double currentMinTemp;
    private double currentMaxTemp;
    private double currentWindSpeed;

    WeatherInfo() {

    }

    public WeatherInfo(double currentTemp, double currentMinTemp, double currentMaxTemp, double currentWindSpeed) {
        this.currentTemp = currentTemp;
        this.currentMinTemp = currentMinTemp;
        this.currentMaxTemp = currentMaxTemp;
        this.currentWindSpeed = currentWindSpeed;
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
