import java.util.HashMap;

public class Optab {

	HashMap<String, Integer> AD, REG, IS, COMP, DL;

	public Optab() {
		AD = new HashMap<String, Integer>();
		REG = new HashMap<String, Integer>();
		IS = new HashMap<String, Integer>();
		COMP = new HashMap<String, Integer>();
		DL = new HashMap<String, Integer>();

		// Assembler Directive
		AD.put("START",1);
		AD.put("END",2);
		AD.put("ORIGIN",3);
		AD.put("EQU",4);
		AD.put("LTORG",5);

		// Registers
		REG.put("AREG",1);
		REG.put("BREG",2);
		REG.put("CREG",3);
		REG.put("DREG",4);

		// Imperative Statements
		IS.put("STOP",0);
		IS.put("ADD",1);
		IS.put("SUB",2);
		IS.put("MULT",3);
		IS.put("MOVER",4);
		IS.put("MOVEM",5);
		IS.put("COMP",6);
		IS.put("BC",7);
		IS.put("DIV",8);
		IS.put("READ",9);
		IS.put("PRINT",10);

		// Declarative
		DL.put("DC", 01);
		DL.put("DS", 02);

		// Comparison
		COMP.put("LT",1);
		COMP.put("LE",2);
		COMP.put("EQ",3);
		COMP.put("GT",4);
		COMP.put("GE",5);
		COMP.put("ANY",6);
	}

	public String getType(String s) {
		if(AD.containsKey(s))
			return "AD";
		else if(IS.containsKey(s))
			return "IS";
		else if(COMP.containsKey(s))
			return "COMP";
		else if(DL.containsKey(s))
			return "DL";
		else if(REG.containsKey(s))
			return "RG";
		return "";
	}
	
	public int getCode(String s) {
		if(AD.containsKey(s))
			return AD.get(s);
		else if(IS.containsKey(s))
			return IS.get(s);
		else if(COMP.containsKey(s))
			return COMP.get(s);
		else if(DL.containsKey(s))
			return DL.get(s);
		else if(REG.containsKey(s))
			return REG.get(s);
		return -1;
	}
	
}
