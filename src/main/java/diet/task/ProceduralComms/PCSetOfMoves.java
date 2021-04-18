/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diet.task.ProceduralComms;

import diet.server.Conversation;
import diet.server.ConversationController.DefaultConversationController;
import diet.server.Participant;
import java.util.Vector;

/**
 *
 * @author gj
 */
public class PCSetOfMoves {
  
    
    //DefaultConversationController cC;
    Vector<Move> moves = new Vector<Move>();
    PCTaskTG pctg;
    
    public PCSetOfMoves(PCTaskTG pctg) {
        this.pctg=pctg;
    }
    
    
    
    public boolean requireRestartOnError = true;
    
    
    
    public boolean evaluate(Participant p, String text){
        
        text = text.toLowerCase();
        
        Move crnt = null;
        for(int i=0;i<moves.size();i++){
            if(!moves.elementAt(i).isSolved()){
                crnt=moves.elementAt(i);
                break;
            }
        }
       
        
        if(crnt == null){
            Conversation.printWSln("Main", "This really shouldn't happen!");
            Conversation.saveErr("The set of moves had no elements in it...");
            return false; //this shouldn't happen
        
            
        } 
        ///Doing reset of all subsequent - this is to be on the safe side
        for(int i=moves.indexOf(crnt)+1;i<moves.size();i++){
            moves.elementAt(i).setSolved(false);
        }
        ///
        double evalresult = crnt.evaluate(p, text);
        if(evalresult==1 && moves.lastElement()==crnt){
           return true;
        }
        else if(evalresult==1 && moves.lastElement()!=crnt){
           return false;
        }
         else if(evalresult==0 ){
            for(int i=0;i<moves.size();i++){
                 moves.elementAt(i).setSolved(false);
            }
            moves.elementAt(0).evaluate(p, text);
            
            return false;
        }
        
          
       return false;
       
    }
    
    public void clearMoves(){
        this.moves=new Vector<Move>();
    }
    
    public void addMoveONLY(Participant performer, String text){
         MoveONLY mo = new MoveONLY(this,performer,text);
         this.moves.add(mo);
    }
    
    public void addMoveANDSAME(Participant performer1, Participant performer2, String text){
         MoveANDSAME mas = new MoveANDSAME(this,performer1,performer2,text);
         this.moves.add(mas);
    }
    
    
    
    
    public void createSequence_MoveONLY(Participant p, String[] text){
        this.moves=new Vector<Move>();
        for(int i=0;i<text.length;i++){
            MoveONLY mo = new MoveONLY(this,p,text[i]);
            this.moves.add(mo);
        }
    }
    
    
     public void createSequence_MoveANDSAME(Participant pRecipient, Participant pDirector, String[] text, boolean[] andsameindices){
        this.moves=new Vector<Move>();
        for(int i=0;i<text.length;i++){
            if(andsameindices[i]){
                 MoveANDSAME mas= new MoveANDSAME(this,pRecipient,pDirector,text[i]); 
                 this.moves.add(mas);
            }
            else{
                 MoveONLY mo = new MoveONLY(this,pRecipient,text[i]);  
                 this.moves.add(mo);
            }
            
           
           
        }
    }
    
    public boolean containsANDSAME(){
        for(int i=0;i<moves.size();i++){
            if (moves.elementAt(i) instanceof MoveANDSAME) return true;
        }
        return false;  
    }
    
    
    public String getSequenceDescription(){
        String sequencedesc ="";
        for(int i=0;i<this.moves.size();i++){
            if(i>0)sequencedesc=sequencedesc+" ";
            sequencedesc = sequencedesc+moves.elementAt(i).getDesc();
        }
        return sequencedesc;
    }
    
    
    public void checkForTimeouts(){
        Move crnt = null;
        for(int i=0;i<moves.size();i++){
            if(!moves.elementAt(i).isSolved()){
                crnt=moves.elementAt(i);
                break;
            }
        }
       
        
        if(crnt == null){
            Conversation.printWSln("Main", "This really shouldn't happen!");
            Conversation.saveErr("This really shouldn`t happen The set of moves had no elements in it...while checking for timeouts");
            return;
        
            
        } 
        if(crnt instanceof MoveANDSAME){
            MoveANDSAME crntMVEANDSAME = (MoveANDSAME)crnt;
            boolean hastimedout= crntMVEANDSAME.isTimedOut();
            if(hastimedout){
                crntMVEANDSAME.setSolved(false);
                this.pctg.displayMovesOnClients_DIRECTOR();
            }    
            
        }
        if(crnt instanceof MoveANDDIFFERENT){
            MoveANDDIFFERENT crntMVEANDDIFFERENT = (MoveANDDIFFERENT)crnt;
            boolean hastimedout= crntMVEANDDIFFERENT.isTimedOut();
            if(hastimedout){
                crntMVEANDDIFFERENT.setSolved(false);
                this.pctg.displayMovesOnClients_DIRECTOR();
            }          
        }
    }
    
    
    
}
