package src.interns.candar;


public class ColorConverter {
	int[] colorArray = new int[512];
	private final int COLOR_MAP = -16711425;
	private final int COLOR_MIN = -65536;
	
	
	public ColorConverter()
	{
		
	}
	
	public ColorConverter(byte[] array)
	{
		for(int i = 0; i < array.length; i++)
		{
			colorArray[i] = (array[i] * (COLOR_MAP)) + COLOR_MIN;
		}
	}
	
	

}
