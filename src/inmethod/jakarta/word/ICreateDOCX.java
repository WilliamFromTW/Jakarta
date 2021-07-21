package inmethod.jakarta.word;


public interface ICreateDOCX {

	public void addReplace(String sOrigText,String sReplaceText) throws Exception;
	
	public void buildDOCX();
	
}
