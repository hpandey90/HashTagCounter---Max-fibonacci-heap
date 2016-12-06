import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.io.*;
class hashtagcounter extends LinkedList{
	public static void main(String args[]){
		BinomialHeap ob = new BinomialHeap();
		String str = "";
		int val = 0;
		HashMap<String,Node> record = new HashMap<>(); //hashmap to store pointer to each hashtag
		try{
			File file = new File("output_file.txt");
			if (!file.exists()) {
				file.createNewFile(); //create new file if not exists
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fw);	
			String fileName = args[0];
			FileReader inputFile = new FileReader(fileName); //input file stream
			BufferedReader bufferReader = new BufferedReader(inputFile);
			String op_str="";
			String line;
			while ((line = bufferReader.readLine()) != null){
				if(line.toLowerCase().matches("stop")){
					 //write output when all query has been processed
					writer.close();
					System.out.println("STOP!");
					System.exit(1);
				}
				HashMap<String,Integer> hash = new HashMap<>(); //hashmap to store extracted ma key from extractMax function
				String[] s = line.split(" ");
				if(s.length==2){
					str = s[0];
					val = Integer.parseInt(s[1]);
					if(record.get(str)!=null){
						ob.increaseKey(record.get(str),val); //increase key if key already present in hashmap record
					}else{
						Node p = ob.insert(str, val); //insert if key is not present
						record.put(str,p);
					}
				}else{
					val = Integer.parseInt(s[0]);
					int cnt=1;
					
					while(cnt<=val){
						Node a = ob.extractMax(); //extract max hashtag from fibonacci heap
						record.remove(a.name); 
						hash.put(a.name,a.data); //store extratced keys in hash
						if(cnt==val)
							op_str=(a.name).replace("#", "")+"\n";
						else
							op_str=(a.name).replace("#", "")+",";
						writer.write(op_str);
						cnt++;
					}
					Iterator it = hash.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pair = (Map.Entry)it.next();
				        String a1 = pair.getKey().toString();
				        int a2 = Integer.parseInt(pair.getValue().toString());
							Node p = ob.insert(a1, a2); //reinsert all extracted key once given query has been addressed
							record.put(a1,p); //reinsert the keys into record hashmap with its pointer
				    }
				}
			}
			
		bufferReader.close();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error while reading file line by line:100003" 
			+ e.getMessage());                      
		}	
	}
	
}
class LinkedList{
	//create linked list data structure to create fibonacci heap
	class Node{
		public int degree = 0;
		public int data;
		public String name;
		public Node child;
		public Node next;
		public Node previous;
		public Node parent;
		public boolean childCut = false; 
		public Node(String i, int s){
			name = i;
			data = s;
		}
	}
	private Node first;
	private Node temp;
	public Node max; 
	public Node head;
	public LinkedList(){
	}
	//insert data into linked list
	Node insert(String i, int s){
		Node list = new Node(i,s);
		if(head==null){ //if linked list is empty create a new node with head and the node itself
			head = new Node("monday",0);
			head.next=list;
			head.previous=list;
			list.next=head;
			list.previous=head;	
		}else{ //else if linked list has more than one node insert next to head
			temp = head.previous;
			temp.next=list;
			list.next=head;
			head.previous=list; 
			list.previous= temp;
		}
		return list;
	}
	//print linked list with linked keys at one level
	void printList(){
		if(head==null){
			System.out.println("List is empty");
		}else{
			Node currentnode = head.next; 
			while(currentnode!=head){
				System.out.println("degree="+currentnode.degree+" data="+currentnode.data);
				currentnode = currentnode.next;
			}
		}
	}
	//print linked list in reverse order with linked keys at one level
	void printListReverse(){
		Node currentnode = head.next; 
		while(currentnode!=head){
			System.out.println(currentnode.name+"==="+currentnode.data);
			currentnode = currentnode.previous;
		}
	}
	//delete all nodes in a linked list
	void deleteAll(){
		Node currentnode = head.previous;
		Node deletenode;
		while(currentnode!=head){
			deletenode =currentnode; 
			currentnode = currentnode.next;
			deletenode.next = null;
			deletenode.previous = null;
		}
		head =null;
	}
	//delete a single node 
	void delete(int arg){
		int count=0;
		Node temp1,temp2;
		if(head==null){
			System.out.println("List is empty");
		}else{
			Node currentnode = head.previous; 
			while(currentnode!=head){
				count++;
				if(count==arg ){
					temp1 = currentnode.previous;
					temp2 = currentnode.next;
					if(temp1==head){
						temp1.previous = temp2;
						temp2.previous = temp1;
					}else if(temp2==head){
						temp1.next = temp2;
						temp2.next = temp1;
					}else{
						temp1.next = temp2;
						temp2.previous = temp1;
					}
				}
				currentnode = currentnode.next;
			}
		}
	}
	//set max pointer on a node with maximum value in fibonacci heap at root level 
	void setMax(){
		int max_val;
		if(head==null){
			System.out.println("List is empty");
		}else{
			Node currentnode = head.next; 
			max_val = currentnode.data; 
			max = currentnode;
			while(currentnode!=head){
				currentnode = currentnode.next;
				if(max_val<currentnode.data){
					max_val = currentnode.data;
					max = currentnode; //max pointer pointing to max key in the heap
				}
			}
		}
	}
}

class BinomialHeap extends LinkedList{
	Node insert(String arg1, int arg2){
		Node node_pointer = super.insert(arg1, arg2); // insert data wrapped in node in fibonacci heap 
		setMax(); // set pointer of max after each new insertions 
		return node_pointer;
	}
	//pairwise combine function to combine all binomial heap with same degree 
	void pairWiseCombine(){
		HashMap<Integer,Node> ob_hash = new HashMap<>(); //hashmap to keep track of heap with same degree
		if(head==null){
			System.out.println("List is empty");
		}else{
			Node currentnode;
			currentnode = head.next; //start from head next 
			ob_hash.put(currentnode.degree,currentnode); //store a heap if not already present in map
			Node hash_prev,temp,temp2;  
			currentnode = currentnode.next;
			while(currentnode!=head){
				if(ob_hash.get(currentnode.degree) != null){ //check if a heap with same degree already exists
					hash_prev = ob_hash.get(currentnode.degree);
					ob_hash.remove(hash_prev.degree); //remove the heap from hashmap and proceed for meld operation
					temp2 = currentnode.next;
					if(hash_prev.degree ==0){ //combine heap with degree 0
						if(hash_prev.data>currentnode.data){
							hash_prev.child = currentnode; //update the child pointer of parent
							hash_prev.child.next =  currentnode;
							hash_prev.child.previous =  currentnode;
							currentnode.parent = hash_prev;
							hash_prev.degree++; //increase the degree which has greater value
							if(ob_hash.get(hash_prev.degree)!=null){ //check if map has heap with same degree 
								linker(ob_hash,hash_prev,(Node) ob_hash.get(hash_prev.degree)); // call linked for pairwise
							}else{
								hash_prev.parent = null;
								hash_prev.childCut = false;
								ob_hash.put(hash_prev.degree, hash_prev); //if map has no exiting degree put the heap in hash
							}							
						}else{
							currentnode.child = hash_prev;
							currentnode.child.next = hash_prev;
							currentnode.child.previous = hash_prev;
							hash_prev.parent = currentnode;
							currentnode.degree++;
							if(ob_hash.get(currentnode.degree)!=null){
								linker(ob_hash,(Node) ob_hash.get(currentnode.degree),currentnode);
							}else{
								currentnode.parent = null;//set parent of node to false since it will be root
								currentnode.childCut = false;//set childcut to false since it will be root
								ob_hash.put(currentnode.degree, currentnode);
							}
							currentnode = currentnode.next;
						}
					}else{
						//if degree is greater than 0
						if(hash_prev.data>currentnode.data){
							temp = hash_prev.child.next; //make smaller node sibling of parent's child 
							hash_prev.child.next = currentnode;
							currentnode.next = temp;
							currentnode.previous = hash_prev.child;
							temp.previous = currentnode;
							currentnode.parent = hash_prev;
							hash_prev.degree++; // increase the degree of the parent
							if(ob_hash.get(hash_prev.degree)!=null){
								linker(ob_hash,hash_prev,(Node) ob_hash.get(hash_prev.degree));
							}else{
								hash_prev.parent = null;
								hash_prev.childCut = false;
								ob_hash.put(hash_prev.degree, hash_prev);
							}
						}else{
							temp = currentnode.child.next;
							currentnode.child.next = hash_prev;
							hash_prev.next = temp;
							hash_prev.previous = currentnode.child;
							temp.previous = hash_prev;
							hash_prev.parent = currentnode;
							currentnode.degree++;
							if(ob_hash.get(currentnode.degree)!=null){
								linker(ob_hash,(Node) ob_hash.get(currentnode.degree),currentnode);
							}else{
								currentnode.parent = null;
								currentnode.childCut = false;
								ob_hash.put(currentnode.degree, currentnode);
							}
						}
					}
					currentnode=temp2;
				}else{
					currentnode.parent=null;
					currentnode.childCut = false;
					ob_hash.put(currentnode.degree, currentnode);
					currentnode = currentnode.next;
				}
			}
			//iterate over all the keys which has a pointer to binomial heaps
			Iterator it = ob_hash.entrySet().iterator();
			//create the fibonacci heap again by linking all the binomial heaps pointer
			head.next = null;//
		    while (it.hasNext()){
		        Map.Entry pair = (Map.Entry)it.next();
		        Node a = (Node) pair.getValue();
				if(head.next==null){
					head.next=a;
					head.previous=a;
					a.next=head;
					a.previous=head;	
				}else{
					Node temp3 = head.previous;
					temp3.next=a;
					a.next=head;
					head.previous=a; 
					a.previous= temp3;
				}
		    }
		}
	}
	//linker does the same as pairwise combine but has a recursive call until it finds a place in the hashmap ob_hash
	void linker(HashMap ob_hash, Node arg1, Node arg2){
		Node temp;
		ob_hash.remove(arg1.degree);
		if(arg1.data>arg2.data){
			temp = arg1.child.next;
			arg1.child.next = arg2;
			arg2.next = temp;
			arg2.previous = arg1.child;
			temp.previous = arg2;
			arg2.parent = arg1;
			arg1.degree++;
			if(ob_hash.get(arg1.degree)!=null){
				linker(ob_hash,arg1,(Node)ob_hash.get(arg1.degree));
			}else{
				arg1.parent=null;
				arg1.childCut = false;
				ob_hash.put(arg1.degree, arg1);
			}
		}else{
			temp = arg2.child.next;
			arg2.child.next = arg1;
			arg1.next = temp;
			arg1.previous = arg2.child;
			temp.previous = arg1;
			arg1.parent = arg2;
			arg2.degree++;
			if(ob_hash.get(arg2.degree)!=null){
				linker(ob_hash,(Node)ob_hash.get(arg2.degree),arg2);
			}else{
				arg2.parent=null;
				arg2.childCut = false;
				ob_hash.put(arg2.degree, arg2);
			}
		}
	}
	//increase key operation has two arguments node and amount by which key has to be increased 
	void increaseKey(Node key, int to){
			key.data = key.data+to; // increase key 
			Node first = head.next;
			Node temp = key.parent;
			if(key.parent!=null && key.data>key.parent.data){ //check if parent of the key is smaller
				if(key.parent.childCut==false){ // if childcut is false then we need to remove only the subtree rooted at key
					if(key.next==key){ // if key has single child
						head.next=key; //remove the key and meld it root level
						key.previous = head;
						key.next = first;
						first.previous = key;
						key.parent.childCut = true;
						key.parent.degree--; // decrease the degree of key's parent
						key.parent.child = null;//disconnect key from its parent
						key.parent = null;
					}else{
						//if key has more then one child
						key.next.previous = key.previous;//remove the key and join its sibilng
						key.previous.next = key.next;
						key.parent.childCut = true; // set childcut to true
						key.parent.degree--;
						key.parent.child = key.next;
						head.next=key; // meld the key at root level
						key.previous = head;
						key.next = first;
						first.previous = key;
						key.parent = null; //set key.parent as null as it has become root 
					}
				}else{
					//if childcut is true it enforces cascading cut
					if(key.next==key){
						head.next=key;
						key.previous = head;
						key.next = first;
						first.previous = key;
						key.parent.childCut = true;
						key.parent.degree--;
						key.parent.child = null;
						key.parent = null;
					}else{
						key.next.previous = key.previous;
						key.previous.next = key.next;
						key.parent.childCut = true;
						key.parent.degree--;
						key.parent.child = key.next;
						head.next=key;
						key.previous = head;
						key.next = first;
						first.previous = key;
						key.parent = null;
					}
					cascadingCut(temp);//recursive call to remove and meld all nodes which has lost more than one child
				}
			}
			setMax(); //update max counter after increase key opeartion is done
	}
	//cascadingCut function is a recursive call to remove each heap rooted at a node which has lost more than one child
	void cascadingCut(Node key){
		Node first = head.next;
		Node temp = key.parent;
		if(key.parent!=null){
			if(key.parent.childCut==false){
				if(key.next==key){
					head.next=key;
					key.previous = head;
					key.next = first;
					first.previous = key;
					key.parent.childCut = true;
					key.parent.degree--;
					key.parent.child = null;
					key.parent = null;
				}else{
					key.next.previous = key.previous;
					key.previous.next = key.next;
					key.parent.childCut = true;
					key.parent.degree--;
					key.parent.child = key.next;
					head.next=key;
					key.previous = head;
					key.next = first;
					first.previous = key;
					key.parent = null;
				}
			}
			else{
				if(key.next==key){
					head.next=key;
					key.previous = head;
					key.next = first;
					first.previous = key;
					key.parent.childCut = true;
					key.parent.degree--;
					key.parent.child = null;
					key.parent = null;
				}else{
					key.next.previous = key.previous;
					key.previous.next = key.next;
					key.parent.childCut = true;
					key.parent.degree--;
					key.parent.child = key.next;
					head.next=key;
					key.previous = head;
					key.next = first;
					first.previous = key;
					key.parent = null;
				}
				cascadingCut(temp);
			}
		}
	}
	//extract max key from the fibonacci heap
	Node extractMax(){
		Node return_deleted = max;
		if(max.child!=null){ //check if max has a child or not
			max.previous.next = max.child;
			Node child_node = max.child.next;
			while(child_node!=max.child){//check if child and max.child pointer matches 
				child_node.parent =null;//set child node's parent to null as it will go for pairwise combine
				child_node = child_node.next; 
			}
			child_node.parent =null;
			child_node.previous.next = max.next; 
			max.next.previous = child_node.previous;//remove the max key from the node
			max.child.previous = max.previous;
			max.child=null;
		}else{
			max.next.previous = max.previous;//if max has no child then remove the node from the linked list 
			max.previous.next = max.next;
		}
		max.next=null;
		max.previous=null;
		pairWiseCombine();//call pairwise combine after max key has been extracted
		setMax();//set max pointer as the max key has been removed from the fibonacci heap
		return return_deleted;
	}
}