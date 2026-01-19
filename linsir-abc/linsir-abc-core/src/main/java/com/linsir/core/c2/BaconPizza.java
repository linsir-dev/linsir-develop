package com.linsir.core.c2;

public class BaconPizza extends Pizza{
        private double weight;

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public BaconPizza() {
        }

        public BaconPizza(String name, int size, double price, double weight) {
            super(name, size, price);
            this.weight = weight;
        }

        @Override
        public String showPizza() {
            return super.showPizza() + "\n培根的克数是： " + weight;
        }
    }

