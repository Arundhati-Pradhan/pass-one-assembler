import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;

public class PassOneAssembler {
	int location_cntr = 0, libtab_ptr = 0, pooltab_ptr = 0, symbol_i = 0, lit_i = 0;
    
	LinkedHashMap<String, TRow> SYMTAB = new LinkedHashMap<String, TRow>();
	ArrayList<TRow> LITTAB = new ArrayList<TRow>();
	ArrayList<Integer> POOLTAB = new ArrayList<Integer>();

	private BufferedReader br;

	public static void main(String[] args) {
		PassOneAssembler passOneAssembler = new PassOneAssembler();
		passOneAssembler.POOLTAB.add(0);
		try {
			passOneAssembler.parseFile();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parseFile() throws Exception {
		String asmCode, code;
		br = new BufferedReader(new FileReader("input.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("IC.txt"));
		Optab optab = new Optab();

		while((asmCode = br.readLine()) != null) {
			
			String splitCode[] = asmCode.split("\\s+");

			// Label
			if(!splitCode[0].isEmpty()) {
				if(SYMTAB.containsKey(splitCode[0]))
					SYMTAB.put(splitCode[0], new TRow(splitCode[0], location_cntr, SYMTAB.get(splitCode[0]).getIndex()));
				else
					SYMTAB.put(splitCode[0],new TRow(splitCode[0], location_cntr, ++symbol_i));
			}

			// START or ORIGIN
			if (splitCode[1].equals("START")) {
				location_cntr = calculateLocationFromExpr(splitCode[2]);
				code = "(AD,01)\t(C," + location_cntr + ")";
				bw.write(code + "\n");
			} else if (splitCode[1].equals("ORIGIN")) {
				location_cntr = calculateLocationFromExpr(splitCode[2]);
				if (splitCode[2].contains("+")) {
					String splits[] = splitCode[2].split("\\+"); 
					code="(AD,03)\t(S," + SYMTAB.get(splits[0]).getIndex() + ")+" + Integer.parseInt(splits[1]);
					bw.write(code+"\n");
				} else if (splitCode[2].contains("-")) {
					String splits[] = splitCode[2].split("\\-"); 
					code="(AD,03)\t(S," + SYMTAB.get(splits[0]).getIndex() + ")-" + Integer.parseInt(splits[1]);
					bw.write(code + "\n");
				} else {
					code = "(AD,03)\t(C," + Integer.parseInt(splitCode[2] + ")");
					bw.write(code + "\n");
				}
			}

			// LTORG
			if (splitCode[1].equals("LTORG")) {
				int ptr = POOLTAB.get(pooltab_ptr);
				for(int j = ptr; j < libtab_ptr; j++) {
					location_cntr++;
					LITTAB.set(j, new TRow(LITTAB.get(j).getSymbol(), location_cntr));
					code = "(DL,01)\t(C," + LITTAB.get(j).symbol + ")";
					bw.write(code + "\n");
				}
				pooltab_ptr++;
				POOLTAB.add(libtab_ptr);
			}

			// EQU
			if (splitCode[1].equals("EQU")) {
				int loc = calculateLocationFromExpr(splitCode[2]);
				if(splitCode[2].contains("+")) {
					String splits[] = splitCode[2].split("\\+");
					code="(AD,04)\t(S," + SYMTAB.get(splits[0]).getIndex() + ")+" + Integer.parseInt(splits[1]);
				} else if(splitCode[2].contains("-")) {
					String splits[] = splitCode[2].split("\\-");
					code = "(AD,04)\t(S," + SYMTAB.get(splits[0]).getIndex() + ")-" + Integer.parseInt(splits[1]);
				} else {
					code = "(AD,04)\t(C," + Integer.parseInt(splitCode[2] + ")");
				}
				bw.write(code + "\n");

				if (SYMTAB.containsKey(splitCode[0]))
					SYMTAB.put(splitCode[0], new TRow(splitCode[0], loc, SYMTAB.get(splitCode[0]).getIndex())) ;
				else
					SYMTAB.put(splitCode[0], new TRow(splitCode[0], loc, ++symbol_i));	 
			}

			if (splitCode[1].equals("DC")) {
				location_cntr++;
				int constant = Integer.parseInt(splitCode[2].replace("'",""));
				code = "(DL,01)\t(C," + constant + ")";
				bw.write(code + "\n");
			} else if (splitCode[1].equals("DS")) {
				int size = Integer.parseInt(splitCode[2].replace("'", ""));
				code = "(DL,02)\t(C,"+size+")";
				bw.write(code + "\n");
				location_cntr = location_cntr + size;
			}

			if (optab.getType(splitCode[1]).equals("IS")) {
				int j = 2;
				String tempCode = "";

				code = "(IS,0" + optab.getCode(splitCode[1]) + ")\t";
				while (j < splitCode.length) {
					splitCode[j] = splitCode[j].replace(",", "");
					if (optab.getType(splitCode[j]).equals("RG")) {
						tempCode += optab.getCode(splitCode[j]) + "\t";
					} else {
						if (splitCode[j].contains("=")) {
							splitCode[j] = splitCode[j].replace("=", "").replace("'", "");
							LITTAB.add(new TRow(splitCode[j], -1, ++lit_i));
							libtab_ptr++;
							tempCode += "(L," + lit_i +")";
						}
						else if (SYMTAB.containsKey(splitCode[j])) {
							int ind = SYMTAB.get(splitCode[j]).getIndex();
							tempCode += "(S,0" + ind + ")"; 
						} else {
							SYMTAB.put(splitCode[j], new TRow(splitCode[j], -1, ++symbol_i));
							int ind = SYMTAB.get(splitCode[j]).getIndex();
							tempCode += "(S,0" + ind + ")";
						}
					}
					j++;
				}
				location_cntr++;
				code = code + tempCode;
				bw.write(code + "\n");
			}
			
			if(splitCode[1].equals("END")) {
				int ptr = POOLTAB.get(pooltab_ptr);
				for(int j = ptr; j < libtab_ptr; j++) {
					location_cntr++;
					LITTAB.set(j, new TRow(LITTAB.get(j).getSymbol(), location_cntr));
					code = "(DL,01)\t(C," + LITTAB.get(j).symbol + ")";
					bw.write(code + "\n");
				}
				pooltab_ptr++;
				POOLTAB.add(libtab_ptr);
				code = "(AD,02)";
				bw.write(code + "\n");
			}
		}

		bw.close();
		printSymbolTable();
		printLiteralTable();
		printPoolTable();
		printICTable();
	}

	void printLiteralTable() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("LITTAB.txt"));
		System.out.println("\nLiteral Table\n");
		for(int i=0; i < LITTAB.size(); i++) {
			TRow row = LITTAB.get(i);
			System.out.println(i + "\t" + row.getSymbol() + "\t" + row.getAddess());
			bw.write((i + 1) + "\t" + row.getSymbol() + "\t" + row.getAddess() + "\n");
		}
		bw.close();
	}

	void printPoolTable() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("POOLTAB.txt"));
		System.out.println("\nPOOLTAB");
		System.out.println("Index\t#first");
		for (int i = 0; i < POOLTAB.size(); i++) {
			System.out.println(i + "\t" + POOLTAB.get(i));
			bw.write((i + 1) + "\t" + POOLTAB.get(i) + "\n");
		}
		bw.close();
	}

	void printSymbolTable() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("SYMTAB.txt"));
		Iterator<String> iterator = SYMTAB.keySet().iterator();
		System.out.println("SYMBOL TABLE");
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			TRow value = SYMTAB.get(key);

			System.out.println(value.getIndex() + "\t" + value.getSymbol() + "\t" + value.getAddess());
			bw.write(value.getIndex() + "\t" + value.getSymbol() + "\t" + value.getAddess() + "\n");
		}
		bw.close();
	}

	public int calculateLocationFromExpr(String str) {
		int temp=0;

		if (str.contains("+")) {
			String splits[] = str.split("\\+");
			temp = SYMTAB.get(splits[0]).getAddess() + Integer.parseInt(splits[1]);
		} else if (str.contains("-")) {
			String splits[] = str.split("\\-");
			temp = SYMTAB.get(splits[0]).getAddess() - (Integer.parseInt(splits[1]));
		} else {
			temp = Integer.parseInt(str);
		}
		return temp;
	}

	public void printICTable() throws FileNotFoundException, IOException {
		br = new BufferedReader(new FileReader("IC.txt"));
		String icCode;
		System.out.println("\nIC");
		while ((icCode = br.readLine()) != null) {
			System.out.println(icCode);
		}
	}
}
