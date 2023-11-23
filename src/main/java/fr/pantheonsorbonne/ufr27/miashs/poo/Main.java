package fr.pantheonsorbonne.ufr27.miashs.poo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    public static void main(String ...args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        ItemsScrapper scrapper  = new ItemsScrapper();
        ArrayList<Item> items = scrapper.parseSource(ContentProxy.getCached());
        System.out.println("Les Attributs de chaque Item (" + items.size()+")");
        for(int i=0 ; i<items.size(); i++){
            System.out.println("Item " + i);
            for(Field field : Item.class.getDeclaredFields()){
                field.setAccessible(true);
                System.out.println(field.getName()+": "+Objects.toString(field.get(items.get(i))));
            }
            System.out.println("------------");
        }
        System.out.println("=======================");
        System.out.println("L'analyse des données collectées");
        ItemAnalyzer itemAnalyzer = new ItemAnalyzer(items);
        for(Method method : ItemAnalyzer.class.getDeclaredMethods()){
            System.out.println(method.getName()+ ": " + Objects.toString(method.invoke(itemAnalyzer)));
        }
    }
    
}
