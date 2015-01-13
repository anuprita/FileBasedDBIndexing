import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MyDatabase {
	String filename = new String("D:/data.db");
	String idIndex = new String("D:/id.ndx");
	String stateIndex = new String("D:/state.ndx");
	String lastNameIndex = new String("D:/lastname.ndx");
	 
	
	
	
	
	public String readFile(long offset)
	{
		try
			{
				RandomAccessFile randomAccessFile = new RandomAccessFile(filename, "rw");
				randomAccessFile.seek(offset);
				int id = randomAccessFile.readInt();
				String fields = randomAccessFile.readUTF();
				randomAccessFile.close();
				String row = id+fields;
				return row;
			}
		catch(FileNotFoundException e)
			{
				System.out.println(filename+" File not found.");
				e.printStackTrace();
			} 
		catch (IOException e) 
			{
				System.out.println("IOError.Problem reading File.");
				e.printStackTrace();
			}
		return "";
	}
	
	public long writeFile(String input)
	{
		long seeker = 0;
		try 
			{
				RandomAccessFile randomAccessFile1 = new RandomAccessFile(filename, "rw");
				File file = new File(filename);
			
				seeker = file.length();
				randomAccessFile1.seek(seeker);
				input = input + "\n";
				String temp = input.substring(0, input.indexOf(','));
				temp = temp.replace("\"", "");
				temp = temp.replace("\'","");
				int id = Integer.parseInt(temp);
				String fields = input.substring(input.indexOf(','));// rest fields
				randomAccessFile1.writeInt(id);
				randomAccessFile1.writeUTF(fields);
				randomAccessFile1.close();
				return seeker;
			} 
		catch (IOException e) 
			{
				System.out.println("Problem Writing File.");
				e.printStackTrace();
			}
		return seeker;
	}
	
	 //Search for a id in the index file and then display data from data.db
	public void searchIndex(String key,int temp)
	{
        try 
        {
            if(temp==1)
            	{
            		RandomAccessFile randomAccessFile2 = new RandomAccessFile(idIndex, "rw");
            		File file = new File(idIndex);
					randomAccessFile2.seek(0);
					byte nameArr[]=new byte[(int)file.length()];
					randomAccessFile2.readFully(nameArr);
					randomAccessFile2.close();
					String completeFile = new String(nameArr);
					boolean present=false;
					String offsetValue = null;
					if(completeFile.length()!=0)
						{
							String rowArray[]=completeFile.split("\n");
							for(int i=0;i<rowArray.length;i++)
								{
									String values[]=rowArray[i].split("  ");
							
									if(values[0].equalsIgnoreCase(key))
										{
											offsetValue = values[1];
											present=true;
											break;
										}
							
								}
							if(present)
								{
									displayRow(key,offsetValue);
								}
							else
								{
									System.out.println("Record with that ID does not exist.");
								}	
													
						}
					else
						{
							System.out.println("The index file is empty!");
						}
				
            	}
            else if(temp==2)
            	{
            		filterRow(key,lastNameIndex);
            	}
            else
            	{
            		filterRow(key, stateIndex);	
					
            	}
			
			
		} 
        catch (FileNotFoundException e) 
        	{
				System.out.println(idIndex+" File not found");
				e.printStackTrace();
        	}
        catch (IOException e) 
        	{
				System.out.println("Problem reading file.");
				e.printStackTrace();
        	}
        

		
	}
	
	
	public void filterRow(String key,String name)
	{
		try {
			RandomAccessFile randomAccessFile3 = new RandomAccessFile(name, "rw");
			File file = new File(name);
			randomAccessFile3.seek(0);
			byte nameArr[]=new byte[(int)file.length()];
			randomAccessFile3.readFully(nameArr);
			randomAccessFile3.close();
			String completeFile = new String(nameArr);
			boolean present = false;
			int indexRow = 0;
			if(completeFile.length()!=0)
				{
					String rowArray[]=completeFile.split("\n");
					for(int i=0;i<rowArray.length;i++)
						{
								if(rowArray[i].contains(key))
								{
									present = true;
									indexRow = i;
									break;
								}
						}
					
					if(present)
					{
						String[] hold = rowArray[indexRow].split("  ");
						for(int i=1;i<hold.length;i++)
						{
							displayRow(key, hold[i]);	
						}
						System.out.println("Total "+(hold.length-1)+" records displayed.");
					}
				}
		} 
		catch (FileNotFoundException e) 
			{
				System.out.println("File Not Found");
				e.printStackTrace();
			} 
		catch (IOException e) 
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}	
			
	}
	
	//get the offset as input and display that row
	public void displayRow(String key,String offsetValue)
	{
		
		try 
			{
			
				String result = readFile(Long.parseLong(offsetValue));
			
				System.out.println("The details for key "+key+" is\n"+result);
			
			} 
		catch (NumberFormatException e) 
		{
			
			e.printStackTrace();
		}
		
	}
	
	//insert a new row to data.db after checking if id already exists and then call functions to insert offset in index files
	public void insertRow(String input)
	{
		try 
        {
			input = input.replace("\'","\"");
			String stateKey = null;
			char[] ca = input.toCharArray();
			int counter = 0;
			int enter = 0;
			for(int i=0;i<ca.length;i++)
			{
				if(ca[i]=='\"')
					counter++;
				if(counter==15)
					{
						stateKey = Character.toString(ca[i+1]);
						stateKey = stateKey.concat(Character.toString(ca[i+2]));
						enter = i;
						break;
					}
				
			}
			List<Character> carray = new ArrayList<Character>();
			
			for(int i=0;i<ca.length;i++)
				carray.add(ca[i]);
			
			if(carray.get(enter+5)!='\"')
			{carray.add(enter+5,'\"');
			carray.add(enter+11,'\"');
			}
			input = "";
			for(int i=0;i<carray.size();i++)
			input = input + carray.get(i);
			//input = input.replace("\"", "");
			
			
			
			String key = input.substring(0, input.indexOf(","));
			key = key.replace("\'","");
			key = key.replace("\"","");
			
			String valuesArray[] = input.split(",");
			String nameKey = valuesArray[2];
			nameKey = nameKey.replace("\'","");
			nameKey = nameKey.replace("\"","");
			
			stateKey = stateKey.replace("\'","");
			stateKey = stateKey.replace("\"","");
			
			
			if(!findKey(key))
				{
					long seeker = writeFile(input); //write to data.db file
					addToIndex(key, seeker); //write to id.ndx
					addToSecondaryIndex(lastNameIndex,nameKey,seeker); //write to lastname.ndx
					addToSecondaryIndex(stateIndex,stateKey,seeker); //write to state.ndx
				}
			else
				{
					System.out.println("No duplicates allowed!");
				}
		} 
        catch (Exception e) 
        	{
				// TODO Auto-generated catch block
				e.printStackTrace();
        	}
	}
	
	//after new entry in data.db, add its offset in primary index file
	public void addToIndex(String key,long offset)
	{
		try 
		{
					
			RandomAccessFile randomAccessFile4= new RandomAccessFile(idIndex,"rw");
			File file=new File(idIndex);
			randomAccessFile4.seek(file.length());
			
			String entry= key + "  " + offset +"\n";
	        randomAccessFile4.write(entry.getBytes());
	        randomAccessFile4.close();
	        sortIndex();
			
		} 
		catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		
	}
	// after new insert in data.db, add new offset to secondary indexes
	public void addToSecondaryIndex(String indexName,String key,long offset)
	{
		try 
			{
				boolean alreadyExists = false;
				List<String> rowArray = new ArrayList<String>();
				RandomAccessFile randomAccessFile5= new RandomAccessFile(indexName,"rw");
				File file=new File(indexName);
			    randomAccessFile5.seek(0);
			
				byte nameArr[]=new byte[(int)file.length()];
				randomAccessFile5.readFully(nameArr);
				String temp = new String(nameArr);
			
				if(temp.length()!=0)
					{
					rowArray = new ArrayList<String>(Arrays.asList(temp.split("\n")));
					 int count = rowArray.size();
					for(int i=0;i<count;i++)
						{
							if(rowArray.get(i).contains(key))
								{
									alreadyExists = true;
									String hold = rowArray.get(i)+"  "+offset;
									
									rowArray.remove(i);
									rowArray.add(i,hold);
								}
						}
				
					if(!alreadyExists)
						{
						    String hold = new String(key + "  " + offset);
							rowArray.add(hold);
						}
					}
				else
					{
						String hold = new String(key + "  " + offset);
						rowArray.add(hold);
					}
				randomAccessFile5.seek(0);
				for(int i=0;i<rowArray.size();i++)
					{
						String t = rowArray.get(i)+"\n";
						randomAccessFile5.write(t.getBytes());
						
					}
				randomAccessFile5.close();
				
				sortSecondaryIndex(indexName);
	        // search for the key, if it exists, append the new offset. Else enter new key and offset at the end. 
				
			// then sort the index file.
			}
		catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
    
	}
	
	//check if id already exists
	public boolean findKey(String key) 
	{
		try 
			{
			Map<Integer,Long> tree = new TreeMap<Integer,Long>();
			RandomAccessFile randomAccessFilex= new RandomAccessFile(idIndex,"rw");
		
			File file=new File(idIndex);
			String f[]=null;
			String temp[] = null;
			
			byte arr[]=new byte[(int) file.length()];
			randomAccessFilex.seek(0);
			randomAccessFilex.readFully(arr);
			randomAccessFilex.close();
			String wholeFile = new String(arr);
			
			// to sort index file
			if(wholeFile.length()!=0)
			{
				f = wholeFile.split("\n");
			
			for(int i=0;i<f.length;i++)
				{	
					temp = f[i].split("  ");
					tree.put(Integer.parseInt(temp[0]),Long.parseLong(temp[1]));
				}
			System.out.println("key to find:"+key);
			
			System.out.println(tree.keySet());
			
			System.out.println("tree VALUE:"+tree.containsKey(Integer.parseInt(key)));
			return tree.containsKey(Integer.parseInt(key));
			}
			randomAccessFilex.close();
			}
		catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//search if key already exists
		
		return false;
	}
	
	// sort the index after inserting a new offset value
	public void sortIndex()
	{
		try 
			{
				// to read from existing index file
			Map<Integer,Long> tree2 = new TreeMap<Integer,Long>();	
			RandomAccessFile randomAccessFile7= new RandomAccessFile(idIndex,"rw");
				randomAccessFile7.seek(0);
				File file=new File(idIndex);
				String f[]=null;
				String temp[] = null;
				byte arr[]=new byte[(int) file.length()];
				randomAccessFile7.readFully(arr);
				String wholeFile = new String(arr);
				
				// to sort index file
				f = wholeFile.split("\n");
				for(int i=0;i<f.length;i++)
					{	
				     temp = f[i].split("  ");
				     tree2.put(Integer.parseInt(temp[0]),Long.parseLong(temp[1]));
					}
			
				//to enter the sorted index into file
				String entry=null;
				randomAccessFile7.seek(0);
		         for(Map.Entry<Integer,Long> a : tree2.entrySet()) 
		         	{
		        	 int key = a.getKey();
		        	 Long value = a.getValue();
	        	  
		        	 entry= key + "  " + value+"\n";
		        	 System.out.println(entry);
		        	 randomAccessFile7.write(entry.getBytes());
		        	 
		         	}
		         randomAccessFile7.close();
			
			} 
		catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	}
	
	//sort secondary indexes after new offset is added
	public void sortSecondaryIndex(String indexName)
	{
		// sort the file using TreeMap
		try 
			{
				Map<String,Long[]> map = new TreeMap<String,Long[]>();	
				RandomAccessFile randomAccessFile8 = new RandomAccessFile(indexName, "rw");
				File file = new File(indexName);
				randomAccessFile8.seek(0);
				byte[] arr = new byte[(int) file.length()];
				randomAccessFile8.readFully(arr);
                String wholeFile = new String(arr);
				
				// to sort index file
				 String f[] = wholeFile.split("\n");
				 String temp[]=null;
				for(int i=0;i<f.length;i++)
					{	
				     temp = f[i].split("  ");
				     Long[] off = new Long[temp.length-1];
				      int j=1;
				      int k =0;
				      while(j<temp.length)
				    	 {
				    	  off[k] = Long.parseLong(temp[j]);
				    	  j++;
				    	  k++;
				    	 }
				      map.put(temp[0],off); 
					}
				//to enter the sorted index into file
				String entry=null;
				randomAccessFile8.seek(0);
		         for(Map.Entry<String,Long[]> a : map.entrySet()) 
		         	{
		        	 String key = a.getKey();
		        	 String valueOffset = "";
		        	 Long value[]=a.getValue();
		        	 
	        	       for(int i=0;i<value.length;i++)
	        	       {
	        	    	   valueOffset = valueOffset + value[i].toString();
	        	    	   if(i!=value.length-1)
	        	    		   valueOffset = valueOffset+"  ";
	        	    	   
	        	       }
	        	       
		        	 entry= key + "  " + valueOffset+"\n";
		        	 System.out.println(entry);
		        	 randomAccessFile8.write(entry.getBytes());
		         	
		         	}
		         randomAccessFile8.close();
			} 
		catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//delete a row from data.db
	public void deleteRow(String key) 
		{
		
			try
			{
				RandomAccessFile randomAccessFile9 = new RandomAccessFile(idIndex, "rw");
				File file = new File(idIndex);
				randomAccessFile9.seek(0);
				byte nameArr[]=new byte[(int)file.length()];
				randomAccessFile9.readFully(nameArr);
				randomAccessFile9.close();
				String completeFile = new String(nameArr);
				boolean present = false;
				int indexRow = 0;
				long offsetDelete = 0;
				if(completeFile.length()!=0)
					{
						String rowArray[]=completeFile.split("\n");
						for(int i=0;i<rowArray.length;i++)
							{
								if(rowArray[i].contains(key))
									{
									String[] temphold = rowArray[i].split("  ");
									if(temphold[0].equals(key))
										{present = true;
										indexRow = i;
										break;
										}
									}
							}
				
						if(present)
							{
								String[] hold = rowArray[indexRow].split("  ");
								// NEW CODE ADDED
								
								offsetDelete = Long.parseLong(hold[1]);
								
								//Delete from data.db
								RandomAccessFile raf = new RandomAccessFile(filename, "rw");
								File f = new File(filename);
								raf.seek(offsetDelete);
								long len = f.length()-offsetDelete;
								byte store[] = new byte[(int)len];
								raf.readFully(store);
								String wholeFile = new String(store);
								int end = wholeFile.indexOf("\n");
								byte b[] = new byte[end];
								for(int i=0;i<b.length;i++)
									b[i]=48;
								raf.seek(offsetDelete);
								raf.write(b);
								raf.close();
							}
						else
							{	
								System.out.println("No record found to delete.");
							}
						
					}	
					//delete from index files
					deleteFromIndex(idIndex,key,offsetDelete);
					deleteFromSecondaryIndex(lastNameIndex,key,offsetDelete);
					deleteFromSecondaryIndex(stateIndex,key,offsetDelete);

			}	
					// go to data.db at this offset and replace line with 0s. Also delete this offset from all indexes
				
	catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	//delete offset from primary index
	public void deleteFromIndex(String name,String key,long offset)
	{
		
		try 
			{
				//String offsetDelete = String.valueOf(offset);
				RandomAccessFile raf2 = new RandomAccessFile(name, "rw");
				File f = new File(name);
				
				raf2.seek(0);
				byte reader[] = new byte[(int)f.length()];
				raf2.readFully(reader);
				
				String wholeFile = new String(reader);
				String rows[] = wholeFile.split("\n");
				boolean present = false;
				int deleteIndex = 0;
				for(int i=0;i<rows.length;i++)
					{
						if(rows[i].contains(key+" "))
							{
								System.out.println("BEfore Deletion, key has been found.");
								present = true;
								deleteIndex = i;
								break;
							}
					}
				List<String> records = new ArrayList<String>(Arrays.asList(rows));
				String reqdRow = rows[deleteIndex];
				String values[] = reqdRow.split("  ");
				if(values.length==2)
					{	
						System.out.println("Remove from Primary index list:"+records.get(deleteIndex));
						records.remove(deleteIndex);
					}
				/*else
					{
						List<String> off = new ArrayList<String>(Arrays.asList(values));
						for(int i=1;i<values.length;i++)
							{
								if(values[i].equals(offsetDelete))
									off.remove(i);	
							}
						String newRow = "";
						int size = off.size();
						for(int i=0;i<size;i++)
							{
								newRow = newRow+off.get(i);
								if(i!=size-1)
									newRow = newRow + "  ";
						
							}
						records.remove(deleteIndex);
						records.add(deleteIndex,newRow);
					}*/
				
				String h = "";
				for(String s : records)
					{
					System.out.println(s);	
					h = h + s + "\n";
					}
				
				byte[] wr = new byte[h.length()];
				wr = h.getBytes();
				raf2.setLength(0); // to empty the old contents
				raf2.seek(0);      // to write from position 0
				raf2.write(wr);
				raf2.close();
			
			} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//delete offset from secondary indexes
	public void deleteFromSecondaryIndex(String name,String key,long offset)
	{
		RandomAccessFile raf3;
		try 
			{
				String offsetDelete = String.valueOf(offset);
				raf3 = new RandomAccessFile(name, "rw");
				File f = new File(name);
			
				raf3.seek(0);
				byte reader[] = new byte[(int)f.length()];
				raf3.readFully(reader);
				
				String wholeFile = new String(reader);
				String rows[] = wholeFile.split("\n");
				boolean present = false;
				int deleteIndex = 0;
				
				for(int i=0;i<rows.length;i++)
					{
						if(rows[i].contains(" "+offsetDelete+" ")||rows[i].contains(" "+offsetDelete))
							{
								//modify code here
								present = true;
								System.out.println("True has been set"+rows[i]+" where index is:"+i);
								deleteIndex = i;
								break;
							}
					}
				List<String> records = new ArrayList<String>(Arrays.asList(rows));
				String reqdRow = rows[deleteIndex];
				System.out.println("Required row is:"+reqdRow);
				String values[] = reqdRow.split("  ");
				if(values.length==2)
					{
					System.out.println("Lets remove:");
					System.out.println(records.get(deleteIndex));	
					records.remove(deleteIndex);
					}
				else
					{
						List<String> off = new ArrayList<String>(Arrays.asList(values));
						for(int i=1;i<values.length;i++)
							{
								if(values[i].equals(offsetDelete))
									off.remove(i);	// delete the offset by using the index of values i.e delete it from off(which is an arraylist)
							}
						String newRow = "";
						int size = off.size();
						for(int i=0;i<size;i++) //convert arrayList to a string again
							{
								newRow = newRow+off.get(i);
								if(i!=size-1)
									newRow = newRow + "  ";
						
							}
						System.out.println("New row after deleting old index:"+newRow);
						records.remove(deleteIndex);  //removing from main records arraylist to add a newly created row of one size less at the same index
						records.add(deleteIndex,newRow);
					}
			
				String h = "";
				for(String s : records)
					{
						h = h + s + "\n";
					}
				byte[] wr = new byte[h.length()];
				wr = h.getBytes();
				raf3.setLength(0);
				raf3.seek(0);
				raf3.write(wr);
				raf3.close();
			
			} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void updateRow(String key,String fieldname,String value)
	{
		try
		{
		RandomAccessFile randomAccessFile8 = new RandomAccessFile(idIndex, "rw");
		File file = new File(idIndex);
		randomAccessFile8.seek(0);
		byte nameArr[]=new byte[(int)file.length()];
		randomAccessFile8.readFully(nameArr);
		randomAccessFile8.close();
		String completeFile = new String(nameArr);
		boolean present=false;
		String offsetValue = null;
		int foundAt = 0;
		if(completeFile.length()!=0)
			{
				String rowArray[]=completeFile.split("\n");
				for(int i=0;i<rowArray.length;i++)
					{
						String values[]=rowArray[i].split("  ");
				
						if(values[0].equalsIgnoreCase(key))
							{
								offsetValue = values[1];
								foundAt = i;
								present=true;
								break;
							}
				
					}
				if(present)
					{
						Long offset = Long.parseLong(offsetValue);
						String row = readFile(offset);
						
						String[] rows = row.split("\"");
						
						deleteRow(key);
						
						//logic to find update column value
						if(fieldname.equalsIgnoreCase("first_name"))
							rows[1]=value;
						else if(fieldname.equalsIgnoreCase("last_name"))
							rows[3]=value;
						else if(fieldname.equalsIgnoreCase("company_name"))
							rows[5]=value;
						else if(fieldname.equalsIgnoreCase("address"))
							rows[7]=value;
						else if(fieldname.equalsIgnoreCase("city"))
							rows[9]=value;
						else if(fieldname.equalsIgnoreCase("county"))
							rows[11]=value;
						else if(fieldname.equalsIgnoreCase("state"))
							rows[13]=value;
						else if(fieldname.equalsIgnoreCase("zip"))
							rows[15]=value;
						else if(fieldname.equalsIgnoreCase("phone1"))
							rows[17]=value;
						else if(fieldname.equalsIgnoreCase("phone2"))
							rows[19]=value;
						else if(fieldname.equalsIgnoreCase("email"))
							rows[21]=value;
						else if(fieldname.equalsIgnoreCase("web"))
							rows[23]=value;
						else
							{
							System.out.println("Field doesnt exist.");
							System.exit(0);
							}
						
						//original location od deleteRow()
						
						
					String output="\""+key+"\",\"";
					
					for(int i=1;i<rows.length;i++)
						{
						if(i!=rows.length-1)
							output = output + rows[i]+"\"";
						}
					System.out.println("Trying to insert:"+output);
					insertRow(output);
					
					}
				else
					{
						System.out.println("Record with that ID does not exist.");
					}	
										
			}
		else
			{
				System.out.println("The index file is empty!");
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
	}
	//show the number of rows in data.db using index entries
	public void showCount() 
	{
		
		try {
			RandomAccessFile raf4 = new RandomAccessFile(idIndex, "rw");
			File file = new File(idIndex);
			raf4.seek(0);
			byte nameArr[]=new byte[(int)file.length()];
			raf4.readFully(nameArr);
			String completeFile = new String(nameArr);
			
			if(completeFile.length()!=0)
				{
					String rowArray[]=completeFile.split("\n");
					System.out.println("Total count of records on file:"+rowArray.length);
				}
			else
			{
				System.out.println("No records in database.");
			}
			raf4.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertCSV()
	{
		try
		{
		File dbfile = new File(filename);
		if(!dbfile.exists())
		{
		System.out.println("Inserting the 500 rows.");
		RandomAccessFile raf5 = new RandomAccessFile("D:/us-500.csv", "r");
		raf5.seek(0);
		// false read to avoid writing the column name
		raf5.readLine();
		//iterate over the remaining 500 rows
		for(int i=0;i<500;i++)
		insertRow(raf5.readLine());
		System.out.println("Done inserting the 500 rows.");
		raf5.close();
		}
		else
		{
			System.out.println("File already exists. So InsertCSV() won't run again.");
		}
		}
		catch(Exception e)
		{
		System.out.println("Error");	
		e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MyDatabase md=new MyDatabase();
		
		
		//uncomment below block to have an interactive UI
		/*System.out.println("Please enter which function is to be performed.");
		System.out.println("1.Search 2. Insert 3. Delete 4. Modify 5. Count");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		try {
			int i = Integer.parseInt(br.readLine());
			switch(i)
			{
			case 1:
				{ 
					
					System.out.println("how do you want to search?");
					System.out.println("1.By id 2. By lastname 3. By state");
					
					String t = br.readLine();
					int temp = Integer.parseInt(t);
					if(temp==1)
					{
						System.out.println("Please enter an id to search");
						String id=br.readLine();
						md.searchIndex(id,temp);
					}
					else if(temp==2)
					{
						System.out.println("Please enter a lastname to search");
						String lName=br.readLine();
						md.searchIndex(lName,temp);
					}
					else if(temp==3)
					{
						System.out.println("Please enter a state to search");
						String state=br.readLine();
						md.searchIndex(state,temp);
					}
					break;
				}
			case 2:
				{
					System.out.println("Enter data to insert.");
					String temp = br.readLine();
					md.insertRow(temp);
					break;
				}
			case 4:
				{
					System.out.println("Enter the primary key to change that row:");
					String key = br.readLine();
					System.out.println("Enter fieldname to change:");
					String fieldname = br.readLine();
					System.out.println("Enter new value for "+fieldname+":");
					String value = br.readLine();
					md.updateRow(key,fieldname,value);
					break;
				}
			case 3:
				{
					System.out.println("Enter ID");
					String key = br.readLine();
					md.deleteRow(key);
					break;
				}
			case 5:
				{
					md.showCount();
					break;
				}
			
			default:
				{
					System.out.println("Please enter a valid integer value (1 to 5)");
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		// interactive UI block ends here. Below is hardcoded function calls.
		
		
		//inserting 500 rows
		//md.insertCSV();
		// show the count of rows
		/*md.showCount();
		
		String newRow = "'501','Rick','Castle','rant, John B Jr','6649 N Blue Gum St','New Orleans','Orleans','LA','70116','504-621-8927','504-845-1427','castle@gmail.com','http://www.castleabc.com'";
		//inserting a new row
		md.insertRow(newRow);
		// showing new count after insertion
		md.showCount();
		//deleting a row with ID=231
		md.deleteRow("231");
		//showing count after deletion
		md.showCount();
		//search for a row using ID
		md.searchIndex("231", 1);
		//search for a row using lastname
		md.searchIndex("Butt", 2);
		//search for a row using State
		md.searchIndex("ID",3);
		//update last_name to new value
		*/
		md.deleteRow("31");
		/*md.deleteRow("13");
		
		//update zipcode to new value
		md.updateRow("330", "zip", "75252");
		*/
	}
	
	

}


