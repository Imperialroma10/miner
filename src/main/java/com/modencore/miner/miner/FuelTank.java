package com.modencore.miner.miner;

public class FuelTank {

    Miner miner;

    int maxfuel = 500;
    int currentfuel = 15;
    public FuelTank(Miner miner){
        this.miner=miner;
    }

    public int getMaxfuel() {
        return maxfuel;
    }

    public int getCurrentfuel() {
        return currentfuel;
    }

    public void setCurrentfuel(int currentfuel) {
        this.currentfuel = currentfuel;
    }

    public boolean addFuel(int fuelcount){
        if (getCurrentfuel() + fuelcount <= getMaxfuel()){
            setCurrentfuel(getCurrentfuel() + fuelcount);
            return true;
        }
        return false;
    }
    public boolean takeFuel(int fuelcount){
        if (getCurrentfuel() - fuelcount >= 0){
            setCurrentfuel(getCurrentfuel() - fuelcount);
            return true;
        }
        return false;
    }
}
