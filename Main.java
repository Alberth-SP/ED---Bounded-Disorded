package com.code;


import java.util.HashSet;

public class Main {

	public static void main(String[] args) {

		BoundedDisorder<Integer> boundedDisorder = new BoundedDisorder<Integer>(2);			
		/* TEST PARA ARBOL BINARIO NORMAL */
		
		int N = 250000;
		int [] arr = new int[N];
		
		int repetidos = 0;
		for(int i= 0; i<N; i++){
			int value = (int)Math.floor(Math.random()*205000+1);
			arr[i] = value;			
			boolean state = boundedDisorder.add(value);		
			if(state == false)  repetidos++;
			
		}
		
		int size = boundedDisorder.size();
		int econtrados=0;
		boundedDisorder.show();	
			
		for(int i= 0; i<N; i++){		
			boolean state = boundedDisorder.search(arr[i]);	
			if(state == true) econtrados++;
			
		}			
		System.out.println("REPETIDOS: "+repetidos+" "+"AGREGADOS: " + size +" Encontrados: "+ (econtrados-repetidos));		
		 
	}

}

