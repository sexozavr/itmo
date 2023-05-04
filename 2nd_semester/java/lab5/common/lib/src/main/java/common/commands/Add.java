package common.commands;

import common.entities.Route;
import common.handler.CollectionHandler;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Add extends CommandWithElement {
    @Override
    public String getName(){
        return "add";
    }

    @Override
    public String getDescription(){
        return getName() + "                             -- add new element to collection\n";
    }

    @Override
    public void execute(CollectionHandler collectionHandler){
        PriorityQueue<Route> collection = collectionHandler.getCollection();

        collection.add(this.route);
        collectionHandler.updateCollection(collection);
    }
}
