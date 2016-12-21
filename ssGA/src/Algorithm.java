/***********************************************/
/** PROBLEM SOLVING                           **/
/** UNIVERSITY OF LUXEMBOURG                  **/
/** DEC 2010                                  **/
/** Prof. Pascal Bouvry                       **/
/** Assistant Patricia Ruiz                   **/
/** Assistant Cesar Diaz                      **/
/***********************************************/

import java.util.Random;

public class Algorithm
{
  private  int          chrom_length; // Alleles per chromosome
  private  int          gene_number;  // Number of genes in every chromosome
  private  int          gene_length;  // Number of bits per gene
  private  int          popsize;      // Number of individuals in the population
  private  double 		pc, pm;      // Probability of applying crossover and mutation
  private  Problem       problem;     // The problem being solved
  private  Population    pop;         // The population
  private  static Random r;           // Source for random values in this class
  private  Individual aux_indiv;  // Internal auxiliar individual being computed
  private  Individual aux_indiv_1; 

  // CONSTRUCTOR
  public Algorithm(Problem p, int popsize, int gn, int gl, double pc, double pm)
  throws Exception
  {
    this.gene_number   = gn;
    this.gene_length   = gl;
    this.chrom_length  = gn*gl;
    this.popsize       = popsize;
    this.pc            = pc;
    this.pm            = pm;
    this.problem       = p;                     
    this.pop = new Population(popsize,chrom_length);// Create initial population
    this.r             = new Random();
    this.aux_indiv     = new Individual(chrom_length);
    this.aux_indiv_1   = new Individual(chrom_length);
    for(int i=0;i<popsize;i++){
	    pop.set_fitness(i, problem.evaluateStep(pop.get_ith(i)));
    }
	pop.compute_stats();
  }

  // BINARY TOURNAMENT
  public Individual select_tournament() throws Exception
  {
    int p1, p2;


    p1 = (int)(r.nextDouble()*(double)popsize/2 + 0.5); // Round and then trunc to int
    												  // Tournament only for the best half
    //if(p1>popsize-1){ p1=popsize-1;}
    
    do
    {  p2 = (int)(r.nextDouble()*(double)popsize/2 + 0.5);  // Round and then trunc to int
	  //    if(p2>popsize-1){ 
	  //  	  p2=popsize-1;
	  //   }

    }
    while (p1==p2);
    if (pop.get_ith(p1).get_fitness()>pop.get_ith(p2).get_fitness())
    return pop.get_ith(p1);
    else
    return pop.get_ith(p2);
  }

//Double POINT CROSSOVER - ONLY ONE CHILD IS CREATED (RANDOMLY DISCARD 
 // DE OTHER)
 public Individual DPX (Individual p1, Individual p2)
 {
   int  rand1, rand2;
   
   rand1 = (int)(r.nextDouble()*(double)chrom_length-1+0.5); // From 0 to L-1 rounded
   rand2 = (int)(r.nextDouble()*(double)chrom_length-1+0.5); // From 0 to L-1 rounded

   if (rand1>rand2){ //rand1 always <rand2
	   int temp=rand1;
	   rand1=rand2;
	   rand2=temp;
   }
   
   if(rand2>chrom_length-1) rand2=chrom_length-1;

   if(r.nextDouble()>pc) // If no crossover then randomly returns one parent
   	return r.nextDouble()>0.5?p1:p2;
   	
   if(p1.get_fitness()<p2.get_fitness()){ //if p1<p2 => change them
	   Individual temp=p1;
	   p1=p2;
	   p2=temp;
   }
	   
   // Copy CHROMOSOME 1
   for (int i=0; i<rand1; i++)
   {
     aux_indiv.set_allele(i,p1.get_allele(i));
   }
   // Copy CHROMOSOME 2
   for (int i=rand1; i<rand2; i++)
   {
     aux_indiv.set_allele(i,p2.get_allele(i));
   }
   for (int i=rand2; i<chrom_length; i++)
   {
     aux_indiv.set_allele(i,p1.get_allele(i));
   }
   return aux_indiv;
 }
  


  // MUTATE A INTEGER CHROMOSOME
  public Individual mutate(Individual p1)
  {
    Random r = new Random();

    aux_indiv.assign(p1);

    for(int i=0; i<chrom_length; i++){
    if (r.nextDouble()<=pm)  // Check mutation bit by bit...
    {
      aux_indiv.set_allele(i,r.nextInt(16));
    }
    }
    return aux_indiv;

  }

  // REPLACEMENT - THE WORST INDIVIDUAL IS ALWAYS DISCARDED
  public void replace(Individual new_indiv) throws Exception
  {
    pop.set_ith(pop.get_worstp(),new_indiv);
    //pop.compute_stats();                  // Recompute avg, best, worst, etc.
  }

  // EVALUATE THE FITNESS OF AN INDIVIDUAL
  private double evaluateStep(Individual indiv)
  {
    return problem.evaluateStep(indiv);
  }

  //Carries out selection, CrossO, Mutation + acception
  public void go_one_step() throws Exception
  {
	pop.sort_pop(0, popsize-1);//sort of population
	aux_indiv.assign( 
			DPX(select_tournament(),select_tournament()) );//Single point crossover
	mutate(aux_indiv);
	aux_indiv.set_fitness(problem.evaluateStep(aux_indiv));
  //  aux_indiv_1= pop.get_ith((int)(r.nextDouble()*(double)popsize/2 + 0.5));
  //  mutate(aux_indiv_1);
  //  replace(aux_indiv_1);
    replace(aux_indiv);//replace by aux_indiv
  }

  public Individual get_solution() throws Exception
  {
    return pop.get_ith(pop.get_bestp());// The better individual is the solution
  }


public int    get_worstp() { return pop.get_worstp(); }
public int    get_bestp()  { return pop.get_bestp();  }
public double get_worstf() { return pop.get_worstf(); }
public double get_avgf()   { return pop.get_avgf();   }
public double get_bestf()  { return pop.get_bestf();  }
public double get_BESTF()  { return pop.get_BESTF();  }

  public Individual get_ith(int index) throws Exception
  {
    return pop.get_ith(index);
  }

  public void set_ith(int index, Individual indiv) throws Exception
  {
    pop.set_ith(index,indiv);
  }
}
// END OF CLASS: Algorithm

