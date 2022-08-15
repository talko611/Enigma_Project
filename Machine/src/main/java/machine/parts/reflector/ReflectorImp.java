package machine.parts.reflector;

import java.util.Map;

public class ReflectorImp implements Reflector {
    private Map<Integer, Integer> entryToExit;
    private final String id;




    public ReflectorImp(Map<Integer,Integer> inputOutPut, String id){
        this.id = id;
        this.entryToExit = inputOutPut;
    }

    @Override
    public int reflect(int input) {
        return entryToExit.get(input);
    }
    @Override
    public String toString(){
        return "Id: " + this.id
                +"\nMapping: "+ this.entryToExit;
    }
    @Override
    public String getId() {
        return id;
    }
}
