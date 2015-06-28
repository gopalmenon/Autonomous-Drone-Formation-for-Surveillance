package menon.cs6100.FinalProject;

public class TestVector {

	public static void main(String[] args) {

		Vector2D vectorID = new Vector2D(299, 480, 87, 418);
		Vector2D vectorDC = new Vector2D(87, 418, 80, 396);
		Vector2D vectorCJ = new Vector2D(80, 396, 365, 0);
		Vector2D vectorJX = new Vector2D(365, 0, 640, 0);
		Vector2D vectorXY = new Vector2D(640, 0, 640, 480);
		Vector2D vectorYI = new Vector2D(640, 480, 299, 480);
		
		Vector2D vectorJW = new Vector2D(365, 0, 0, 0);
		Vector2D vectorWV = new Vector2D(0, 0, 0, 480);
		Vector2D vectorVI = new Vector2D(0, 480, 299, 480);

		System.out.println("IDxDC = " + vectorID.crossProduct(vectorDC));
		System.out.println("DCxCJ = " + vectorDC.crossProduct(vectorCJ));
		System.out.println("CJxJX = " + vectorCJ.crossProduct(vectorJX));
		System.out.println("JXxXY = " + vectorJX.crossProduct(vectorXY));
		System.out.println("XYxYI = " + vectorXY.crossProduct(vectorYI));
		System.out.println("YIxID = " + vectorYI.crossProduct(vectorID));
		System.out.println("CJxJW = " + vectorCJ.crossProduct(vectorJW));
		System.out.println("JWxWV = " + vectorJW.crossProduct(vectorWV));
		System.out.println("WVxVI = " + vectorWV.crossProduct(vectorVI));
		
	}

}
