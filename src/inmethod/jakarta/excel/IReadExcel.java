package inmethod.jakarta.excel;

import java.util.Vector;

public interface IReadExcel {

	/**
	 * @return get all sheet name
	 */
	public Vector getSheetNames() throws Exception;

	public String getSheetCellData(String sSheetName, int iRow, short shortCell);

	public String getSheetCellData(int iSheetNum, int iRow, short shortCell);

	public int getSheetMaxRowNum(int iSheetNum);

	public int getSheetMaxRowNum(String sSheetName);

	public Vector getAllExcelData() throws Exception;

}