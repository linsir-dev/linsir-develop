package com.linsir.abc.pdai.oopdemo;

import java.util.ArrayList;
import java.util.List;

public class PetOwner {
    private String ownerName;
    // composition: owner has animals
    private List<Animal> pets = new ArrayList<>();

    public PetOwner(String ownerName) {
        this.ownerName = ownerName;
    }

    public void adopt(Animal animal) {
        pets.add(animal);
        System.out.println(ownerName + " adopted " + animal.getName());
    }

    // Polymorphism: call speak() on Animal references
    public void makeAllSpeak() {
        for (Animal a : pets) {
            a.speak(); // runtime dispatch
        }
    }

    // Use instanceof and interface cast for interface-specific behavior
    public void walkAll() {
        for (Animal a : pets) {
            if (a instanceof Walkable) {
                ((Walkable) a).walk();
            } else {
                System.out.println(a.getName() + " cannot walk (not Walkable).");
            }
        }
    }

    @Override
    public String toString() {
        return ownerName + "'s pets: " + pets;
    }
}