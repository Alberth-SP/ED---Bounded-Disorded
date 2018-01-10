package com.code;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author alberth Calla
 * ESTRUCTURA DE DATOS BOUNDED DISORDED: CONSISTE DE UNA ESTRUCTURA DE DATOS CON NODOS INTERNOS COMO INDICES Y  HOJAS DE HASH, LAS CUALES CONTINEN LOS DATOS QUE INSERTEMOS.
 * Para la estructura de Datos con Arbol Binario normal se implemento todas las operaciones (insercion, borrado y busqueda).
 * Para la estructura de Datos con Arbol AVL se implemento implemento busqueda e insercion. 
 */
public class BoundedDisorder<T extends Comparable<T> > {
	private int lenghtHash;			//tamaño del HashSet
	private Node<T> root;
	private int size;

	private static int CHILD_RIGHT = 1;
	private static int CHILD_LEFT = 0;
	private int sideEmpty = -1;  // Bandera que indica si hay un HasSet vacio. 0->Left, 1->right
	private boolean isWasDivided = false;  //bandera que indica si hubo una division para arbol AVL

	public  boolean isEmpty(){
		return (this.size == 0);
	}

	public int size(){
		return this.size;
	}

	public BoundedDisorder(int length){
		assert(length > 1); //debe ser de un tamaño mayor a 1
		this.root = new NodeH();
		this.lenghtHash = length;
	}

	public boolean add(T e){  
		assert(e != null);
		boolean state =  addNodeAVL(null, root, e, -1);   // usamos el arbol normal		
		isWasDivided = false;
		return state;
	}

	/* Metodo para mostrar elementos del Arbol */
	public void show(){
		showNode(root);
	}

	/* Metodo para recorrer elementos del Arbol y acceder al Hash */
	private void showNode(Node<T> current){
		assert(current != null);

		if (current.isLeaft()) {
			HashSet<T> hash = current.getHashSet();
			Iterator<T> it = hash.iterator();

			System.out.print("HashSet : [ ");

			while (it.hasNext())
				System.out.print(it.next() + " ");

			System.out.print("]");
			System.out.println();
			return;
		}
		showNode(current.getRight());
		showNode(current.getLeft());
	}

	/* Metodo iterativo para agregar elementos al Arbol binario Normal*/
	private boolean addNode(Node<T> father,  Node<T> current, T data){
		Node<T> tempCurrent = current;
		Node<T> tempFather = father;

		int sideChild = CHILD_LEFT; // asignacion por defecto ya qeu siempre se cambia

		while(!tempCurrent.isLeaft()){
			T index = tempCurrent.getData();
			tempFather = tempCurrent;

			if(index.compareTo(data) > 0){
				tempCurrent = tempCurrent.getLeft();
				sideChild = CHILD_LEFT;                
			}
			else{
				tempCurrent = tempCurrent.getRight();  
				sideChild = CHILD_RIGHT;
			}
		}

		if(tempCurrent.isLeaft()){
			HashSet<T> hash = tempCurrent.getHashSet();
			if(hash.size() < lenghtHash){
				boolean res = hash.add(data);
				if(res) size++;

				return res;
			}
			return divideNode(tempFather, tempCurrent, data, sideChild);            
		}        
		return false;        //sentencia que nunca se ejecutará                
	}

	/* Metodo para dividir nodo del arbol*/
	private boolean divideNode(Node<T> father, Node<T> current, T data, int sideChild){
		assert(current != null);
		assert(sideChild > -2 && sideChild < 2);

		Iterator<T> it = current.getHashSet().iterator();       

		ArrayList<T> arr = new ArrayList();		
		while(it.hasNext())
			arr.add(it.next());		       

		Collections.sort(arr);
		HashSet<T> oldHash = current.getHashSet();        
		HashSet<T> newHash = new  HashSet<T>();        

		int index = 0;
		for(; index<(lenghtHash/2) ; index++){
			newHash.add(arr.get(index));
			oldHash.remove(arr.get(index));         
		}

		T newData = arr.get(index);
		Node<T> newNodeRig = new NodeH(oldHash);       
		Node<T> newNodeLef = new NodeH(newHash);

		Node<T> newNode = new NodeI<T>(newData);
		newNode.setLeft(newNodeLef);
		newNode.setRight(newNodeRig);
		// CASO DE TRATARSE DE LA RAIZ
		if(current == root){ 
			root = newNode;
			return addNode(null, root, data);
		}        
		// CASO DE UN NODO INTERNO
		if(sideChild == CHILD_LEFT) father.setLeft(newNode);
		else if(sideChild == CHILD_RIGHT) father.setRight(newNode);

		return addNode(father, newNode, data);
	}

	/* metodo para eliminar elementos del Arbol*/
	public boolean remove(T e){
		return remove(null, root, e, -1);
	}

	/* metodo para eliminar elementos del Hash*/
	private boolean remove(Node<T> father, Node<T> current, T data, int mySide){
		assert(current != null);
		assert(mySide > -2 && mySide < 2);		

		if(current.isLeaft()){			
			HashSet<T> hash = current.getHashSet();
			boolean state =  hash.remove(data);	

			if(state) this.size--;
			if(state && (hash.size() == 0)){				
				if(current == root) return true; // En caso de raiz solo retornamos									
				sideEmpty = mySide;				 //establecemos el lado vacio
			}
			return state;
		}

		boolean state;
		T index = current.getData();
		int resComp = index.compareTo(data);

		if(resComp > 0 )				
			state = remove(current, current.getLeft(),data,CHILD_LEFT);
		else
			state = remove(current, current.getRight(),data,CHILD_RIGHT);

		if(state && (sideEmpty == CHILD_LEFT || sideEmpty == CHILD_RIGHT))
			deleteNodes(father, current, mySide );			

		return state;
	}

	private void deleteNodes(Node<T> father, Node<T> current, int mySide ){
		assert(sideEmpty == CHILD_LEFT || sideEmpty == CHILD_RIGHT);	
		assert(mySide > -2 && mySide < 2);
		assert(current != null);
		/* En caso de ser raiz */
		if(current == root) {			
			root = (sideEmpty == CHILD_LEFT) ? current.getRight(): current.getLeft();			
			sideEmpty = -1;
			return;
		}
		/* En caso de nodos internos */
		if(sideEmpty == CHILD_LEFT){ // si elimino hoja izquierda
			if(mySide == CHILD_RIGHT)
				father.setRight(current.getRight());			
			else if(mySide == CHILD_LEFT)
				father.setLeft(current.getRight());

		}else if(sideEmpty == CHILD_RIGHT){ // si elimino hoja derecha
			if(mySide == CHILD_RIGHT)
				father.setRight(current.getLeft());
			else if(mySide == CHILD_LEFT)
				father.setLeft(current.getLeft());
		}
		sideEmpty = -1;
	}



	/* Metodo para la busqueda de un elemento */
	public boolean search(T e) {
		assert(e != null);
		return contains(root, e);
	}

	/* Metodo para la busqueda en HASH */
	private boolean contains(Node<T> current, T e) {		
		assert(current != null);		

		if (current.isLeaft()) {
			HashSet<T> hash = current.getHashSet();
			return hash.contains(e);
		}

		if (current.getData().compareTo(e) > 0) 
			return contains(current.getLeft(), e);
		else 
			return contains(current.getRight(), e);

	}

	/* METODO PARA AGREGAR ELEMENTO A HASH DE ARBOL AVL */
	private boolean addNodeAVL(Node<T> father,  Node<T> current, T data, int sideChild){
		assert(current != null);
		assert(sideChild > -2 && sideChild < 2);
		boolean state = false;

		if(current.isLeaft()){
			HashSet<T> hash = current.getHashSet();

			if(hash.size() < lenghtHash){
				state = hash.add(data);
				if(state) size++; 
				return state;
			}
			return divideNodeAVL(father, current, data, sideChild);			
		} 		

		T index = current.getData();
		int fe = current.getFactor();

		if(index.compareTo(data) > 0){
			state =  addNodeAVL(current, current.getLeft(), data, CHILD_LEFT);

			if(state && isWasDivided){
				current.setFactor(fe-1);
				if((fe - 1) < -1)
					balance(father, current, sideChild, (fe-1));					
			}				
		}
		else{
			state = addNodeAVL(current, current.getRight(), data, CHILD_RIGHT);

			if(state && isWasDivided){					
				current.setFactor(fe+1);
				if((fe+1) > 1 )
					balance(father, current, sideChild, (fe+1));				
			}
		}
		return state;
	}

	/* METODO PARA ROTACION SIMPLE DERECHA DE ARBOL AVL */
	private void RotateRight(Node<T> father, Node<T> current, Node<T> left, int sideCurrent) {		
		assert(current != null && left != null);
		assert(sideCurrent == CHILD_RIGHT || sideCurrent == CHILD_LEFT);

		current.setFactor(0);
		left.setFactor(0);

		current.setLeft(left.getRight());
		left.setRight(current);
		if(father == null) root = left;
		else{
			if(sideCurrent == CHILD_RIGHT) father.setRight(left);
			else father.setLeft(left);
		}			
	}

	/* METODO PARA ROTACION DOBLE DERECHA DE ARBOL AVL */
	private void RotateDoubleRight(Node<T> father, Node<T> current, Node<T> left, int sideCurrent) {		
		assert(current != null && left != null);
		assert(sideCurrent == CHILD_RIGHT || sideCurrent == CHILD_LEFT);	
		Node<T> temp = left.getRight();

		left.setRight(temp.getLeft());
		temp.setLeft(left);
		current.setLeft(temp);
		left.setFactor(0);	

		RotateRight(father, current,current.getLeft(), sideCurrent);	
	}

	/* METODO PARA ROTACION DOBLE IZQUIERDA DE ARBOL AVL */
	private void RotateDoubleLeft(Node<T> father, Node<T> current, Node<T> right, int sideCurrent) {		
		assert(current != null && right != null);
		assert(sideCurrent == CHILD_RIGHT || sideCurrent == CHILD_LEFT);		
		Node<T> temp = right.getLeft();

		right.setLeft(temp.getRight());
		temp.setRight(right);
		current.setRight(temp);
		right.setFactor(0);	

		RotateLeft(father, current,current.getRight(), sideCurrent);				
	}

	/* METODO PARA ROTACION SIMPLE IZQUIERDA DE ARBOL AVL */
	private void RotateLeft(Node<T> father, Node<T> current, Node<T> right, int sideCurrent) {		
		assert(current != null && right != null);
		assert(sideCurrent == CHILD_RIGHT || sideCurrent == CHILD_LEFT);

		current.setFactor(0);
		right.setFactor(0);
		current.setRight(right.getLeft());
		right.setLeft(current);

		if(father == null) root = right;
		else{
			if(sideCurrent == CHILD_RIGHT) father.setRight(right);
			else father.setLeft(right);
		}
	}

	/* METODO PARA BALANCEAR ARBOL AVL */
	private void balance(Node<T> father,  Node<T> current, int sideChild,int fe){
		assert(current != null);
		assert(sideChild == CHILD_RIGHT || sideChild == CHILD_LEFT);
		assert(fe == 2 || fe == -2);

		if(fe == -2){
			if(current.getLeft().getFactor() > 0) 
				RotateDoubleRight(father,current, current.getLeft(),sideChild);
			else 
				RotateRight(father, current, current.getLeft() ,sideChild);			

		}else if(fe == 2){
			if(current.getRight().getFactor() < 0) 
				RotateDoubleLeft(father,current, current.getRight(),sideChild);
			else
				RotateLeft(father, current,current.getRight(), sideChild);			
		}
		isWasDivided = false;
	}

	/* METODO PARA DIVIDIR ARBOL CON AVL */
	private boolean divideNodeAVL(Node<T> father, Node<T> current, T data, int sideChild){
		assert(current != null);
		assert(sideChild > -2 && sideChild < 2);		
		Iterator<T> it = current.getHashSet().iterator();       

		ArrayList<T> arr = new ArrayList();
		while(it.hasNext())
			arr.add(it.next());		

		Collections.sort(arr);
		HashSet<T> oldHash = current.getHashSet();        
		HashSet<T> newHash = new  HashSet<T>();        

		int i=0;
		for(; i<(lenghtHash/2) ; i++){
			newHash.add(arr.get(i));
			oldHash.remove(arr.get(i));         
		}

		T newData = arr.get(i);
		Node<T> newNodeRig = new NodeH<T>(oldHash);       
		Node<T> newNodeLef = new NodeH<T>(newHash);

		Node<T> newNode = new NodeI<T>(newData);
		newNode.setLeft(newNodeLef);
		newNode.setRight(newNodeRig);

		boolean state = false;
		if(current == root){ 
			root = newNode;
			return  addNodeAVL(null, root, data,-1);
			
		}          
		
		if(sideChild == CHILD_LEFT) father.setLeft(newNode);
		else if(sideChild == CHILD_RIGHT) father.setRight(newNode);
        state = addNodeAVL(father, newNode, data, -1);
		isWasDivided = true;
		return state;
	}

	/* INTERFAZ PARA NODOS */
	interface Node<T>{

		public T getData();
		boolean isLeaft();        
		public Node<T> getRight();
		public Node<T> getLeft();
		public void setRight(Node<T> right);
		public void setLeft(Node<T> left);     

		public HashSet<T> getHashSet();
		public int getFactor();  
		public void setFactor(int factor);

	}

	/* NODO INTERNO (INDICE) */
	class NodeI<T extends Comparable<T>> implements Node<T>, Comparable<NodeI<T>>{

		Node<T> right;
		Node<T> left;
		T data; 
		int factor=0;

		public NodeI(){
			this.data = null;
			this.right = null;
			this.left = null;
		}

		public NodeI(T data){
			this.data = data;
			this.right = null;
			this.left = null;

		}


		@Override
		public T getData() {
			return this.data;
		}

		@Override
		public boolean isLeaft() {
			return false;
		}

		@Override
		public Node<T> getRight() {
			return right;
		}

		@Override
		public Node<T> getLeft() {
			return left;
		}

		@Override
		public int compareTo(NodeI<T> o) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public HashSet<T> getHashSet() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setRight(Node<T> right) {
			this.right = right;
		}

		@Override
		public void setLeft(Node<T> left) {
			this.left = left;
		}   


		@Override
		public int getFactor() {
			// TODO Auto-generated method stub
			return this.factor;
		}

		@Override
		public void setFactor(int factor) {
			this.factor = factor;

		} 

	}

	/* NODO HOJA(HASH) */
	class NodeH<T> implements Node<T>{         

		HashSet<T> hash;

		public NodeH(){
			this.hash = new HashSet<T>();
		}

		public NodeH(HashSet<T> hash){
			this.hash = hash;
		}

		public NodeH(NodeH node){
			this.hash = node.hash;
		}

		public HashSet<T> getHashSet(){
			return this.hash;
		}

		@Override
		public T getData() {
			throw new UnsupportedOperationException("Not supported.");
		}

		@Override
		public boolean isLeaft() {
			return true;
		}

		@Override
		public Node<T> getRight() {
			throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public Node<T> getLeft() {
			throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setRight(Node<T> right) {
			throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setLeft(Node<T> left) {
			throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public int getFactor() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setFactor(int factor) {
			//TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		} 

	}





}