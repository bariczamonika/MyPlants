package ie.dbs.myplants;

public class WeatherInfo {
    private double currentTemp;
    private double currentMinTemp;
    private double currentMaxTemp;
    private double currentWindSpeed;

    public WeatherInfo() {

    }

    public WeatherInfo(double currentTemp, double currentMinTemp, double currentMaxTemp, double currentWindSpeed) {
        this.currentTemp = currentTemp;
        this.currentMinTemp = currentMinTemp;
        this.currentMaxTemp = currentMaxTemp;
        this.currentWindSpeed = currentWindSpeed;
    }

    public double getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
    }

    public double getCurrentMinTemp() {
        return currentMinTemp;
    }

    public void setCurrentMinTemp(double currentMinTemp) {
        this.currentMinTemp = currentMinTemp;
    }

    public double getCurrentMaxTemp() {
        return currentMaxTemp;
    }

    public void setCurrentMaxTemp(double currentMaxTemp) {
        this.currentMaxTemp = currentMaxTemp;
    }

    public double getCurrentWindSpeed() {
        return currentWindSpeed;
    }

    public void setCurrentWindSpeed(double currentWindSpeed) {
        this.currentWindSpeed = currentWindSpeed;
    }

}
