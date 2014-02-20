import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;
public class MainClass {
	private static String Key="&key=52ddafbe3ee659bad97fcce7c53592916a6bfd73";
	private static String baseURL="http://api.zappos.com";
	private static String URL2="/Search/?&term=&excludes=[%22brandName%22,%22originalPrice%22,%22productUrl%22,%22thumbnailImageUrl%22,%22percentOff%22,%22styleId%22,%22productName%22]&sort={%22price%22:%22asc%22}&limit=100&page=";
	private static char quotes='"'; 
	private static ArrayList id = new ArrayList();
	private static ArrayList price =  new ArrayList();
	private static ArrayList colorId =  new ArrayList();
	private static ArrayList Pmax = new ArrayList();
	private static ArrayList Pmin = new ArrayList();
	static ArrayList combinations[];
	static int mainN;
	static float Price;
	private void getData(float P) throws Exception {
		int page=1;
		boolean requestSuccess;
		float endprice=0;
		System.out.println("Fetching product list from Zappos.com");
		while(P>=endprice){
			URL zappos = new URL(baseURL+URL2+page+Key);
			page++;
			requestSuccess = false;
			BufferedReader in=null; 
			do
			{
				try{
					in = new BufferedReader(
					new InputStreamReader(zappos.openStream()));
				requestSuccess = true;
				}
				catch(Exception e)
				{
					//requestSuccess = false;
					Thread.sleep(10);
				}
			}while(!requestSuccess);
			String inputLine;
			String totalLine="";
			while ((inputLine = in.readLine()) != null){
				totalLine=totalLine+inputLine;
				//System.out.println(inputLine);
			}
			in.close();
			JsonParserFactory factory=JsonParserFactory.getInstance();
			JSONParser parser=factory.newJsonParser();
			Map jsonData=parser.parseJson(totalLine);
			Set keyset= jsonData.keySet();
			Iterator itr = keyset.iterator();

			ArrayList results = (ArrayList)jsonData.get("results");
			itr = results.iterator();

			HashMap temp;

			for(int i=0;itr.hasNext();i++)
			{

				temp  = (HashMap)itr.next();
				price.add(Float.parseFloat(temp.get("price").toString().substring(1)));
				if(i==0){
					Pmin.add(Float.parseFloat(temp.get("price").toString().substring(1)));
					//System.out.println(Float.parseFloat(temp.get("price").toString().substring(1)));
				}
				//key =itr.next().getClass();
				id.add(Integer.parseInt(temp.get("productId").toString()));
				colorId.add(Integer.parseInt(temp.get("colorId").toString()));
				
				//System.out.println(Integer.parseInt(temp.get("productId").toString()));
				//System.out.println(temp.get("price").toString().substring(1));
				endprice=Float.parseFloat(temp.get("price").toString().substring(1));
			}
			Pmax.add(endprice);
			//System.out.println(endprice);
			// System.out.println(endprice);
		}
		System.out.println("Done");
	}
	private void nsum(int N, int P, int[] testArray){
		if(N==1){
			testArray[0]=P;
			for(int i=0;i<mainN;i++){
				combinations[i].add(testArray[i]);
			}

		}
		else{
			if(P==0){
				testArray[N-1]=0;
				nsum(N-1,0,testArray);
			}
			else if(P==1){
				testArray[N-1]=1;
				nsum(N-1,0,testArray);
			}
			else{
				for(int i=P;i>=(P+(P%2))/2;i--){
					//System.out.println(P/2);
					testArray[N-1]=i;
					nsum(N-1,P-i,testArray);
				}
			}
		}

	}
	private void printDetails(int[] testArray){


		for(int index1=0;index1<combinations[0].size();index1++){
			ArrayList indexMin=new ArrayList();
			ArrayList indexMax=new ArrayList();
			boolean breakflag=false;
			for(int index2=0;index2<mainN;index2++){
				float Lprice = (float)((int)combinations[index2].get(index1))/((float)(mainN*100))*Price;
				if (Lprice<(float)price.get(0)||((float)(Lprice*1.0099))<(float)price.get(0)){
					breakflag=true;
				}
				if(Lprice>(float)price.get(price.size()-1)||((float)(Lprice*1.0099))>(float)price.get(price.size()-1)){
					breakflag=true;
				}
			}
			if(breakflag){
				continue;
			}
			for(int index2=0;index2<mainN;index2++){
				float Lprice = (float)((int)combinations[index2].get(index1))/((float)(mainN*100))*Price;
				indexMin.add(binarySearch(Lprice));
				indexMax.add(binarySearch((float)(Lprice*1.0099)));
			}
			recursiveprint(mainN,indexMin,indexMax,testArray);	
		}

	}
	private static int binarySearch(float key){
		int low = 0;
		int high = price.size() - 1;
		while(high >= low) {
			int middle = (low + high) / 2;
			if((float)price.get(middle) == key) {
				return middle;
			}
			if((float)price.get(middle) < key) {
				low = middle + 1;
			}
			if((float)price.get(middle) > key) {
				high = middle - 1;
			}
		}

		if(high>=0){
			return high;
		}
		else{
			return low;
		}

	}
	private static void recursiveprint(int N,ArrayList indexMin,ArrayList indexMax, int[] testArray){
		if((indexMin.size()==0)||(indexMax.size()==0)){

		}
		else{
			if(N==1){
				for(int i= (int)indexMin.get(0); i<=(int)indexMax.get(0); i++){
					float totalprice=0;
						System.out.println((1)+" product id : "+ id.get(i)+" color ID : "+colorId.get(i)+" price : "+ price.get(i));
						totalprice=totalprice+(float)price.get(i);
					for(int j=1;j<mainN;j++){
						System.out.println((j+1)+" product id : "+ id.get(testArray[j])+" color ID : "+ colorId.get(testArray[j])+" price : "+ price.get(testArray[j]));
						totalprice=totalprice+(float)price.get(testArray[j]);
					}
					System.out.println("Total Price is "+totalprice);
					System.out.println(" ");
					
				}
			}
			else{
				for(int i=(int)indexMin.get(N-1); i<=(int)indexMax.get(N-1); i++){
					testArray[N-1]=i;
					recursiveprint(N-1,indexMin,indexMax,testArray);
				}
			}
		}
	}
	public static void main(String[] args) throws Exception {
		//input the mainN (number of products), Price (Total cost in dollors) as command line arguments
		mainN=Integer.parseInt(args[0]);
		Price=Integer.parseInt(args[1]);;
		MainClass OB=new MainClass();
		OB.getData(Price);
		combinations=new ArrayList[OB.mainN];
		for(int i=0;i<OB.mainN;i++){
			combinations[i]=new ArrayList();
		}

		int[] testArray = new int[mainN];
		int[] testArray1 = new int[mainN];
		long startTime = System.currentTimeMillis();
		OB.nsum(mainN,100*mainN,testArray);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		//System.out.println(totalTime);
		OB.printDetails(testArray1);

	}
}
