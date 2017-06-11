package internal;

import java.util.ArrayList;

import util.FileUtil;

enum TokenType {
    NONE, ID, I_CONST, F_CONST, STR_CONST, C_CONST, 
    PC/*punctuation*/, OP, TOKEN_END;
}

public class LexicalAnalyzer {
	//buffers for the output tokens
	private static ArrayList<TokenType> tokenType
		= new ArrayList<TokenType>();
	private static ArrayList<Object> tokenValue 
		= new ArrayList<Object>();
	
	//push a token into the buffer
	private void pushToken(TokenType type, Object o){
		tokenType.add(type);
		tokenValue.add(o);
	}
	
	//get the total number of all tokens
	public static int getTokenNum(){
		return tokenType.size();
	}
	
	// get token type by index
	public static TokenType getType(int index){
		return tokenType.get(index);
	}
	
	// get token value by index
	public static Object getValue(int index){
		return tokenValue.get(index);
	}
	
	public void process(String iFile, String oFile){
		System.out.println("Lexical analyzing...");
		
		System.out.println("input file:" + iFile);
		System.out.println("output file:" + oFile);
		
		ArrayList<String> strs = FileUtil.readFileByLines(iFile);
		
		//remember the current state
		TokenType state = TokenType.NONE;
		// string buffer
		String lexeme = "";
		char preChar = ' ';
		for(String s: strs){
			int index = s.indexOf(":");
			if(index <= 0) {
				continue;
			}
			int lineNum = Integer.parseInt(s.substring(0, index));
			s = s.substring(index + 1);
			//System.out.println("line number:" + lineNum + "str(" + s + ")");

			for(int i = 0; i < s.length(); i++){
				char c = s.charAt(i);
				if(state == TokenType.NONE){ // if we are not in any state
					if(isDigit(c)){		// start a new constant			 
						state = TokenType.I_CONST;
						lexeme += c;
					}else if(isLetter(c)){
						state = TokenType.ID;
						lexeme += c;
					}else if(c == '\"'){	// start a new string literal
						state = TokenType.STR_CONST;
						lexeme += c;
					}else if(c == ',' || c == ';' || c == '('
							|| c == ')' || c == '{' || c == '}'){
						System.out.println("PC: " + c);
						this.pushToken(TokenType.PC, c);
					}else if(c == '+' || c == '-' || c == '*' 
							|| c == '/' || c == '='){
						if(i < s.length() - 1 && s.charAt(i + 1) == '='){
							System.out.println("OP: " + c + '=');
							this.pushToken(TokenType.OP, c + "=");
							i++;
						}else{
							System.out.println("OP: " + c);
							this.pushToken(TokenType.OP, c);
						}
					}else{
						if(c == '\0'){			// break and goto next line
							lexeme = "";
							break;
						}
						if(isWhiteSpace(c)){	// skip the white space
							lexeme = "";
							continue;
						}
					}
				}else if(state == TokenType.ID){
					if(isDigit(c) || isLetter(c)){
						lexeme += c;
					}else{	// an ID is found
						System.out.println("ID: " + lexeme);
						this.pushToken(TokenType.ID, lexeme);
						state = TokenType.NONE;
						lexeme = "";
						i--;
					}
				}else if(state == TokenType.I_CONST){
					if(isDigit(c)){
						lexeme += c;
					}else if(c == '.'){
						state = TokenType.F_CONST;
						lexeme += c;
					}else{
						if(isPunctuation(c) || isOperator(c) || isWhiteSpace(c)){
							System.out.println("I_CONST: " + lexeme);
							this.pushToken(TokenType.I_CONST, lexeme);
							state = TokenType.NONE;
							lexeme = "";
							i--;
						}else{
							System.out.println("invalid const!");
							i = skipError(s, i);
						}
					}
				}else if(state == TokenType.F_CONST){
					if(isDigit(c)){
						lexeme += c;
					}else{
						System.out.println("F_CONST: " + lexeme);
						this.pushToken(TokenType.F_CONST, lexeme);
						state = TokenType.NONE;
						lexeme = "";
						i--;
					}
				}else if(state == TokenType.STR_CONST){
					if((c == '\"' || c == 'n'	// escape sequence 
					   || c == '0' || c == 'r') && preChar == '\\'){
						lexeme += c;
					}
					else if(c == '\"'){
						lexeme += c;
						System.out.println("String_CONST: " + lexeme);
						this.pushToken(TokenType.STR_CONST, lexeme);
						state = TokenType.NONE;
						lexeme = "";
					}else{
						lexeme += c;
					}
				}
				else{
					System.out.println("There is somthing wrong here!");
					System.out.println("current state=" + state + ", c=" + c);
				}
				
				preChar = c;
			}
		}
		
		this.pushToken(TokenType.TOKEN_END, "");
	}
	
	int skipError(String s, int i){
		int index = i;
		for(; index < s.length(); index++){
			char c = s.charAt(index);
			if(c == ',' || c == ';' || c == '\n' || isWhiteSpace(c)){
				return index - 1;
			}
		}
		return s.length() - 1;
	}
	// test if the input character c is punctuation
	private boolean isPunctuation(char c){
		if(c == ',' || c == ';' || c == '(' || c == ')'
			|| c == '[' || c == ']' || c == '{' || c == '}'){
			return true;
		}
		return false;
	}
	// test if the input character c is an operator
	private boolean isOperator(char c){
		if(c == '+' || c == '-' || c == '/' || c == '*'
			|| c == '=' || c == '|' || c == '&' || c == '^'
			|| c == '!' || c == '<' || c == '>'){
			return true;
		}
		return false;
	}
	//test if the input character c is white space
	private boolean isWhiteSpace(char c){
		if(c == ' ' || c == '\t'){
			return true;
		}
		return false;
	}
	private boolean isLetter(char c) {
		if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'){
			return true;
		}
		
		return false;
	}
	private boolean isDigit(char c){
		if(c >= '0' && c <= '9'){
			return true;
		}
		
		return false;
	}
}
