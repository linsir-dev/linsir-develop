package com.linsir.abc.pdai.Base.obj.oopdemo;

public class Main {
    public static void main(String[] args) {
        // Inheritance + Encapsulation: Dog/Cat extend Animal (private fields, getters/setters)
        Animal dog = new Dog("Rex", 3, "Labrador");
        Animal cat = new Cat("Mittens", 2, true);

        // Polymorphism: speak() calls are dispatched to the concrete implementations
        dog.speak(); // Dog implementation
        cat.speak(); // Cat implementation

        // Concrete method from base class
        dog.eat("bone");
        cat.eat("fish");

        // Interface usage
        if (dog instanceof Walkable) {
            Walkable w = (Walkable) dog; // downcast to access Walkable
            w.walk();
            w.stroll(); // default method
        }

        // Composition and polymorphism through PetOwner
        PetOwner owner = new PetOwner("Alice");
        owner.adopt(dog);
        owner.adopt(cat);

        System.out.println(owner);
        owner.makeAllSpeak();
        owner.walkAll();

        // Demonstrate modifying encapsulated state via setters
        dog.setName("Rexie");
        System.out.println("Renamed dog: " + dog.getName());
    }
}