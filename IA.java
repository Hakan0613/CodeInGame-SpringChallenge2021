import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    private static grille grilleDeJeu;
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numberOfCells = in.nextInt(); // 37
        
        grilleDeJeu = new grille();
        
        for (int i = 0; i < numberOfCells; i++) {
            int[] voisin= new int[6];
            int index = in.nextInt(); // 0 is the center cell, the next cells spiral outwards
            int richness = in.nextInt(); // 0 if the cell is unusable, 1-3 for usable cells
            int neigh0 = in.nextInt(); // the index of the neighbouring cell for each direction
            voisin[0]=neigh0;
            int neigh1 = in.nextInt();
            voisin[1]=neigh1;
            int neigh2 = in.nextInt();
            voisin[2]=neigh2;
            int neigh3 = in.nextInt();
            voisin[3]=neigh3;
            int neigh4 = in.nextInt();
            voisin[4]=neigh4;
            int neigh5 = in.nextInt();
            voisin[5]=neigh5;
            grilleDeJeu.ajouterCase(richness, voisin);
        }
        

        // game loop
        while (true) {
            int day = in.nextInt(); // the game lasts 24 days: 0-23
            int nutrients = in.nextInt(); // the base score you gain from the next COMPLETE action
            int sun = in.nextInt(); // your sun points
            int score = in.nextInt(); // your current score
            int oppSun = in.nextInt(); // opponent's sun points
            int oppScore = in.nextInt(); // opponent's score
            boolean oppIsWaiting = in.nextInt() != 0; // whether your opponent is asleep until the next day
            int numberOfTrees = in.nextInt(); // the current amount of trees
            
            for (int i = 0; i < numberOfTrees; i++) {
                int cellIndex = in.nextInt(); // location of this tree
                
                int size = in.nextInt(); // size of this tree: 0-3
                
                boolean isMine = in.nextInt() != 0; // 1 if this is your tree
                
                boolean isDormant = in.nextInt() != 0; // 1 if this tree is dormant
                
                grilleDeJeu.updateTree(cellIndex, size, isMine, isDormant);
                
            }
            int directionSoleil = day%6;
            





            int numberOfPossibleActions = in.nextInt(); // all legal actions
            if (in.hasNextLine()) {
                in.nextLine();
            }
            for (int i = 0; i < numberOfPossibleActions; i++) {
                String possibleAction = in.nextLine(); // try printing something from here to start with
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            

            // GROW cellIdx | SEED sourceIdx targetIdx | COMPLETE cellIdx | WAIT <message>
            String action = grilleDeJeu.IAction(day, sun);
            //grilleDeJeu.celluleMax();
            System.out.println(action);
            
            
        }
    }
    
  
}

class grille {
    private ArrayList<cellule> laGrille;
    private int indiceArbreTemp=0;  //Donnée utiliser par la méthode seedTree
    public grille(){
        laGrille = new ArrayList<cellule>();
    }
    public void ajouterCase(int pRichesse, int[] pCaseVoisine){
        cellule nouvelleCase = new cellule(pRichesse, pCaseVoisine);
        this.laGrille.add(nouvelleCase);

    }
    public void updateTree( int indice, int pTaille, boolean pLeMien, boolean pStatut)
    {
        if(this.laGrille.get(indice).getTree()==null)   //s'il n'y a pas d'arbre
        {
            cellule celluleTemp = this.laGrille.get(indice);
            celluleTemp.ajouterArbre(pTaille, pLeMien, pStatut);
            this.laGrille.set(indice,celluleTemp);
        }
        else
        {
            //cellule afficher = this.laGrille.get(indice);
            //System.err.println("Not Updated  taille " + afficher.getTree().getTaille() +" statut "+ afficher.getTree().getStatut());
            this.laGrille.get(indice).setTree(pTaille, pLeMien, pStatut);
            //System.err.println("update realiser :" + indice);
            //afficher = this.laGrille.get(indice);
            //System.err.println("Updated  taille " + afficher.getTree().getTaille() +" statut "+ afficher.getTree().getStatut());
        }
    }

    public String IAction(int day, int pointSoleil)     //Cerveau ;)
    {
        String action;
        if(24-day<=this.getNbArbreTaille(3))
        {
            //System.err.println("Cas complete arbre");
            if(pointSoleil>=4)
            {
                action=this.completeTree();
            }
            else
                action="WAIT";
        }
        else{
            if((this.getArbreMax(pointSoleil)!=-1 )|| ((this.getNbArbreTaille(0) + this.getNbArbreTaille(1) + this.getNbArbreTaille(2)) >3 && this.getArbreMax(pointSoleil)!=-1 ))
            {
                if(this.laGrille.get(this.celluleMax()).getRichesse()> this.laGrille.get(this.getArbreMax(pointSoleil)).getRichesse() && this.calculPS(-1)<= pointSoleil && this.getNbArbreTaille(0)<2 && pointSoleil >= this.getNbArbreTaille(3)*4+ this.calculPS(0))   //Si une cellule vide à une richesse supérieur à mes arbre
                {
                    action=this.seedTree(this.celluleMax());
                }
                else
                {
                    if(pointSoleil >= this.getNbArbreTaille(3)*4+ this.calculPS(0))
                    {
                        action = "GROW " + this.getArbreMax(pointSoleil);
                    }
                    else
                        action="WAIT";
                }
            }
            else
            {
                if(this.calculPS(-1)<= pointSoleil && pointSoleil >= this.getNbArbreTaille(3)*4+ this.calculPS(0))
                {
                    action=this.seedTree(this.celluleMax());
                }
                else
                    action= "WAIT";
            }
        }
        
        return action;
    }

    private String seedTree(int indice)
    {
        return "SEED "+this.indiceArbreTemp + " "+ indice;
    }

    private String completeTree()
    {
        int indiceMax=0;
        int richesseMax=0;
        int cpt=0;
        for (cellule i : laGrille) {
            if(i.getTree()!=null)
            {
               //System.err.println(cpt); 
               if(i.getTree().getAppartenace()==true && i.getTree().getTaille()==3 && richesseMax<i.getRichesse() && i.getTree().getStatut()==false)
                {
                    indiceMax=cpt;
                    richesseMax= i.getRichesse();
                    
                }
            }
            cpt++;
        }
        this.deleteTrees(indiceMax);
        return "COMPLETE "+indiceMax;
    }

    private void deleteTrees(int indice)
    {
        this.laGrille.get(indice).deleteTree();
    }

    public int celluleMax()  //Renvoie la cellule  vide avec le max de richesse atteigneable dans le périmètre d'un arbre
    {
        int indiceMax = 0;
        int richesseMax=-1;
        
        int cpt = 0;
        for (cellule i : laGrille) {
            if(i.getTree()!=null)
            {
                if(i.getTree().getAppartenace()==true && i.getTree().getTaille()!=3)
                {
                    //System.err.println("mon arbre "+cpt);
                    
                    int taille = laGrille.get(cpt).getTree().getTaille();
                    if(taille>0)
                    {
                        int[] caseVoisine1 = laGrille.get(cpt).getCaseVoisine();
                        for(int perimetre1 =0; perimetre1<6; perimetre1++)
                        {
                            int indiceVoisin = caseVoisine1[perimetre1];
                            if(indiceVoisin!=-1)
                            {
                                int richesseVoisin = laGrille.get(indiceVoisin).getRichesse();
                                if(laGrille.get(indiceVoisin).getTree()==null && richesseVoisin>richesseMax )
                                {
                                    indiceMax = indiceVoisin;
                                    richesseMax = richesseVoisin;  
                                    this.indiceArbreTemp = cpt;
                                }
                                if(taille>1)
                                {
                                    int[] caseVoisine2 = laGrille.get(indiceVoisin).getCaseVoisine();
                                    for(int perimetre2 =0; perimetre2<6; perimetre2++)
                                    {
                                        indiceVoisin = caseVoisine2[perimetre2];
                                        if(indiceVoisin!=-1)
                                        {
                                            richesseVoisin = laGrille.get(indiceVoisin).getRichesse();
                                            if(laGrille.get(indiceVoisin).getTree()==null && richesseVoisin>richesseMax)
                                            {
                                                indiceMax = indiceVoisin;
                                                richesseMax = richesseVoisin;
                                                this.indiceArbreTemp = cpt;
                                            }           
                                            if(taille>2)
                                            {
                                                int[] caseVoisine3 = laGrille.get(indiceVoisin).getCaseVoisine();
                                                for(int perimetre3 =0; perimetre3<6; perimetre3++)
                                                {
                                                    indiceVoisin = caseVoisine3[perimetre3];
                                                    if(indiceVoisin!=-1)
                                                    {
                                                        richesseVoisin = laGrille.get(indiceVoisin).getRichesse();
                                                        if(laGrille.get(indiceVoisin).getTree()==null && richesseVoisin>richesseMax)
                                                        {
                                                            indiceMax = indiceVoisin;
                                                            richesseMax = richesseVoisin;
                                                            this.indiceArbreTemp = cpt;
                                                        }  
                                                    }         
                                                }
                                            }  
                                        } 
                                    }
                                    
                                }
                            }             
                        }
                    }
                }
                
            }
            cpt++;
        }
        //System.err.println("l'indice max de la case  est " + indiceMax);
        return indiceMax;
    }
    private int getArbreMax(int pointSoleil)//renvoie l'arbre avec le max de richesse
    {
        arbre arbreMax = new arbre(-1, true, false);
        int indiceMax = -1;   //vaut -1 s'il n'y a pas suffisament de PS
        int cpt=0;
        boolean possible = false; //permet de vérifier s'il y a un arbre max à faire grandir compte tenu des points soleil
        for (cellule i : laGrille) {
            arbre temp = i.getTree();
            if(temp!=null)
            {
                if(arbreMax.getTaille()<temp.getTaille() && temp.getAppartenace()==true && temp.getStatut()== false && pointSoleil>= this.calculPS(temp.getTaille()) && temp.getTaille()!=3)
                {
                    arbreMax = temp;
                    indiceMax=cpt;
                }
            }
            cpt++;
        }
        return indiceMax;
    }

    private int calculPS(int taille) //Calcul le nb de point soleil neccessaire
    {
        int pointSoleil=0;
        switch (taille){
            case -1 :
                pointSoleil=this.getNbArbreTaille(0);
                break;
            case 0:
                    pointSoleil=1+this.getNbArbreTaille(1);
                    break;
            case 1 :
                pointSoleil=3+this.getNbArbreTaille(2);
                break;
            case 2 :
                pointSoleil=7+this.getNbArbreTaille(3);
                break;
        }
        return pointSoleil;

    }
    private int getNbArbreTaille(int taille) //Renvoie le nb d'arbre de taille n en possession
    {
        int nbArbre=0;
        for (cellule i : laGrille) {
            if(i.getTree()!=null)
            {
                if(i.getTree().getAppartenace()==true && i.getTree().getTaille()==taille)
                {
                    nbArbre++;
                }
            }
        }
        return nbArbre;
    }


}

class cellule {
    private int[] caseVoisine;
    private int richesse; 
    private arbre unArbre;

    public cellule(int pRichesse, int[] pCaseVoisine){
        this.richesse=pRichesse;
        this.caseVoisine=pCaseVoisine; 
        this.unArbre = null;       
    }
    
    public void deleteTree()
    {
        this.unArbre=null;
    }

    public void ajouterArbre(int pTaille, boolean pLeMien, boolean pStatut)
    {
        arbre nouvelleArbre = new arbre( pTaille,  pLeMien,  pStatut);
        this.unArbre = nouvelleArbre;
    }
    
    public int getRichesse()
    {
        return this.richesse;
    }
    public int[] getCaseVoisine()
    {
        return this.caseVoisine;
    }
    
    public void setTree(int pTaille, boolean pLeMien, boolean pStatut)
    {
        this.unArbre.setTree(pTaille, pLeMien, pStatut);
    }
    public arbre getTree()
    {
        return this.unArbre;
    }
}

class arbre{
    private int taille;
    private boolean leMien;    //Vrai si m'appartient
    private boolean statut;    //Vrai s'il l'arbre est en someille
    public arbre(int pTaille, boolean pLeMien, boolean pStatut)
    {
        this.taille= pTaille;
        this.leMien = pLeMien;
        this.statut = pStatut;
    }

    public void setTree(int pTaille, boolean pLeMien, boolean pStatut)
    {
        this.taille= pTaille;
        this.leMien = pLeMien;
        this.statut = pStatut;
    }
    public int getTaille()
    {
        return this.taille;
    }

    public boolean getAppartenace()
    {
        return leMien;
    }

    public boolean getStatut()
    {
        return this.statut;
    }
}
