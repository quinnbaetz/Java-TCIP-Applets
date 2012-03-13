package anl.lpsolver;
/*
 *
 *  Revised by Yu WEI
 *  Summer 2004
 *  email: weiy3@mcmaster.ca
 *
 *  Written by Timothy J. Wisniewski
 *  Summer 1996
 *  email:  tjw@euler.bd.psu.edu
 *
 *  Updated by Joe Czyzyk
 *  September - October 1996
 *
 *  Copyright 2004 Optimization Technology Center
 *
 */

import java.awt.*;

public class Matrix
{
  public float A[][];
  private int size;
  private float [] temp;
  
  public Matrix(int size)
    {
      A         = new float[size][size];
      temp      = new float[size];
      this.size = size;
    } /* end Matrix procedure */
  
  /* The solve function does Gaussian Elimination with partial
     pivoting.  In the process it destroys the matrix and the vector b.  */
  
  public boolean solve(float [] x, float [] b)
    { 
      int   row, col, k, i, orig;
      int   swap;
      float max;
      float scale, temp;

      /* This is the forward elimination part of the code */
      
      for (col = 0; col < size-1; col++) {

	/* find maximum element in column col */

	max = Math.abs(A[col][col]);
	swap = col;
	for (i = col+1; i < size; i++)
	  if (Math.abs(A[i][col]) > max) {
	      max = Math.abs(A[i][col]);
	      swap = i;
	    }

	/* exchange rows (interchange rows "swap" and "col" ) */

	if(swap != col) {
	  temp = b[swap];
	  b[swap] = b[col];
	  b[col] = temp;
	  for(i=0; i<size; i++) {
	    temp = A[swap][i];
	    A[swap][i] = A[col][i];
	    A[col][i] = temp;
	  }
	}
	
	/* add multiple of pivot row to each row */

	if (A[col][col] != 0)
	  for (row = col+1; row < size; row++) {
	    scale = A[row][col]/A[col][col];
	    b[row] -= (scale *b[col]);
	    for (k = col; k < size; k++) {
	      A[row][k] -= (scale*A[col][k]);
	    }
	  }
	}

      /* The forward elimination is now complete */

      /* This is the backward substitution part of the code */
      
      for (col = size-1; col >= 0; col--) {

	x[col] = b[col];
	for (row = size-1; row > col; row--)
	  x[col] -= (x[row] * A[col][row]);

	if (A[col][col] != 0)
	  x[col] /= A[col][col];
      }
      
      return true;
    } /* end solve procedure */
  
} /* end Matrix class */
