
 /// Change to remove haha
   /// Add the control group...





/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diet.server.ConversationController;

import diet.attribval.AttribVal;
import diet.server.Conversation;
import diet.server.ConversationController.ui.CustomDialog;
import diet.server.ConversationController.ui.JInterfaceMenuButtonsReceiverInterface;
import diet.server.ConversationController.ui.JInterfaceTwelveButtons;
import diet.server.Participant;
import diet.task.CustomizableReferentialTask.CustomizableReferentialTask;
import diet.task.CustomizableReferentialTask.CustomizableReferentialTaskSettings;
import diet.textmanipulationmodules.haha.HahaVariantGenerator;
import diet.tg.TelegramMessageFromClient;
import diet.tg.TelegramParticipant;
import java.util.Hashtable;
import java.util.Vector;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 *
 * @author LX1C
 */
public class Telegram_dyadic_AskFor_Language_NL_EN_REMOVEHAHA extends TelegramController implements JInterfaceMenuButtonsReceiverInterface{

    
//CustomizableReferentialTaskSettings crts = new CustomizableReferentialTaskSettings(this, true, null, null );
    
    
    
    JInterfaceTwelveButtons jitb = new JInterfaceTwelveButtons(this,"Assign to groups","start", "pause", "", "", "", "", "","","");

    /**
     * Creates new form JInterfaceFiveButtons
     */
    
    //JInterfaceMenuButtonsReceiverInterface
    
    
    Hashtable htCRT = new Hashtable();
    
    
    public Telegram_dyadic_AskFor_Language_NL_EN_REMOVEHAHA(Conversation c) {
        super(c);
        
    }

    public Telegram_dyadic_AskFor_Language_NL_EN_REMOVEHAHA(Conversation c, long istypingtimeout) {
        super(c, istypingtimeout);
       
    }

    
    
    Vector<TelegramParticipant> vQueuedNL = new Vector();
    Vector<TelegramParticipant> vQueuedOTHER = new Vector();
    
   
     public synchronized void telegram_participantJoinedConversation(TelegramParticipant p) {
         //Synchronized because of manual group assignment by experimenter
         
        // if(this.experimentHasStarted=false){
             if(p.getConnection().getLogincode().toUpperCase().endsWith("NL")){
                 Conversation.printWSln("Main", "Queuing "+p.getConnection().telegramID + " to NL");
                 vQueuedNL.add(p);
             }
             else{
                 Conversation.printWSln("Main", "Queuing "+p.getConnection().telegramID + " to OTHER");
                 vQueuedOTHER.add(p);
             }
            Conversation.printWSln("Main", "Current queue size: NL QUEUE: "+vQueuedNL.size()+" OTHER QUEUE: "+this.vQueuedOTHER.size());
        // }
    }

    @Override
    public void performActionTriggeredByUI(String s) {
        if(s.equalsIgnoreCase("Assign to groups")){
             assignQueueParticipantsAndStart(true);
        }    
    }
     
    
    public  Vector randomize(Vector vSource){
        if (vSource.size()<2)return vSource;
        Vector vNEW = new Vector();
        for(int i = 0;i < vSource.size();i++){
            vNEW.insertElementAt(vSource.elementAt(i), r.nextInt(vNEW.size()+1));
           
        }
        return vNEW;
    }
    
    
     
        
        
        
        
        
         int createdSubdialogue = 0;
    
     public synchronized void assignQueueParticipantsAndStart(boolean doHalfControls){
         
        Conversation.printWSln("Main", "Randomizing order of queued participants");
        this.vQueuedNL = randomize(this.vQueuedNL);
        this.vQueuedOTHER = randomize (this.vQueuedOTHER);
         
         
        if(vQueuedNL.size() +  vQueuedOTHER.size() <2){
            CustomDialog.showDialog("Can't start with NL: "+vQueuedNL.size()+ "    OTHER: "+this.vQueuedOTHER.size());
            return;
        }
        
         if(vQueuedNL.size()  % 2 ==1 ){
               //CustomDialog.showDialog();
               boolean proceed = CustomDialog.getBoolean("There is an uneven ("+ this.vQueuedNL.size() +") number of NL pairs!", "continue", "cancel");
               if(!proceed)return;
         }
         if(vQueuedOTHER.size()  % 2 ==1 ){
               //CustomDialog.showDialog();
               boolean proceed = CustomDialog.getBoolean("There is an uneven ("+ this.vQueuedOTHER.size() +")number of OTHER pairs!", "continue", "cancel");
               if(!proceed)return;
         }
         
         
         ///have an option of pairing them differently, e.g. simply take the first element and put it at the end. This will pair them differently
         ///Also need control condition 
         
         
         while(this.vQueuedNL.size()>1){
             TelegramParticipant tp1 = this.vQueuedNL.elementAt(0);
             TelegramParticipant tp2 = this.vQueuedNL.elementAt(1);
             
             this.createNewPair(tp1, tp2);
                
             
             this.vQueuedNL.remove(tp1);
             this.vQueuedNL.remove(tp2);   
             
             
             
             
              
             
                
         }
         
         while(this.vQueuedOTHER.size()>1){
             TelegramParticipant tp1 = this.vQueuedOTHER.elementAt(0);
             TelegramParticipant tp2 = this.vQueuedOTHER.elementAt(1);
             
             
              this.createNewPair(tp1, tp2);
             
             this.vQueuedOTHER.remove(tp1);
             this.vQueuedOTHER.remove(tp2);      
         }
         
         
         
         
         if(vQueuedNL.size() ==0 &&  vQueuedOTHER.size() ==0   ){
             CustomDialog.showDialog("All participants have been paired!");
         }
         else if(vQueuedNL.size() ==1 &&  vQueuedOTHER.size() ==0   ){
             CustomDialog.showDialog("One NL participant remaining");
         }
         else if(vQueuedNL.size() ==0 &&  vQueuedOTHER.size() ==1   ){
             CustomDialog.showDialog("One OTHER participant remaining");
         }
         else if (vQueuedNL.size() ==1 &&  vQueuedOTHER.size() ==1   ){
             //CustomDialog.showDialog("There is one mixed pair possible");
             boolean option = CustomDialog.getBoolean("There is one of both types left over. Assign to mixed group?", "assign to mixed", "wait for others to login");
             if(option){
                 TelegramParticipant tp1 = this.vQueuedNL.elementAt(0);
                 TelegramParticipant tp2 = this.vQueuedOTHER.elementAt(0);     
               
                 this.createNewPair(tp1, tp2);
                  
                 this.vQueuedNL.remove(tp1);
                 this.vQueuedOTHER.remove(tp2);    
                 
             }
             
         }
         else{
             CustomDialog.showDialog("This shouldn't happen: "+this.vQueuedNL.size()+ ": "+ this.vQueuedOTHER.size());
         }

    }
    
    Vector experimentalParticipants = new Vector();
    
    public void createNewPair(TelegramParticipant tp1, TelegramParticipant tp2){
         createdSubdialogue=createdSubdialogue+1;
         String name = "";
         if(createdSubdialogue %2 ==1){
             name = "experimental"+createdSubdialogue;
             pp.createNewSubdialogue(name,tp1,tp2); 
             Conversation.printWSln("Main", "Created new pair"+tp1.getConnection().getValidLogincode()+ " and "+tp2.getConnection().getValidLogincode());
             c.telegram_sendInstructionToParticipant_MonospaceFont(tp1, "Please start!");
             c.telegram_sendInstructionToParticipant_MonospaceFont(tp2, "Please start!");
             this.experimentalParticipants.add(tp1);
             this.experimentalParticipants.add(tp2);

             
             
        
         }
         else{
             name = "control"+createdSubdialogue;
             pp.createNewSubdialogue(name,tp1,tp2); 
             Conversation.printWSln("Main", "Created new pair"+tp1.getConnection().getValidLogincode()+ " and "+tp2.getConnection().getValidLogincode());
             c.telegram_sendInstructionToParticipant_MonospaceFont(tp1, "Please start!");
             c.telegram_sendInstructionToParticipant_MonospaceFont(tp2, "Please start!");
             
        
         }
        
        
    
    
           
           
            
    }
    
  
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
     
  
     
     
     
    
     
     
   
    @Override
    public void telegram_participantReJoinedConversation(TelegramParticipant p) {
        
         vQueuedOTHER.add(p);
         Conversation.printWSln("Main", "Current queue size: NL QUEUE: "+vQueuedNL.size()+" OTHER QUEUE: "+this.vQueuedOTHER.size());
       
    }
     

    @Override
    public void telegram_processTelegramMessageFromClient(TelegramParticipant sender, TelegramMessageFromClient tmfc) {
        if(tmfc.u.hasMessage()  && tmfc.u.getMessage().hasText()){
             String text=tmfc.u.getMessage().getText();
            // this.crt.processChatText(sender, text);
             if(!text.startsWith("/")){
                  this.processMessageFromClient(sender, tmfc);
                 
                  // c.telegram_relayMessageTextToOtherParticipants(sender, tmfc);      
                //  c.telegram_relayMessageTextToOtherParticipants(sender, tmfc);
             } 
             else{
                 CustomizableReferentialTask crt = (CustomizableReferentialTask)this.htCRT.get(sender);
                 if(crt!=null){
                     crt.processChatText(sender, text);
                 }
             }
      
        }
        if(this.relayPhotos && tmfc.u.hasMessage()&&  tmfc.u.getMessage().hasPhoto()){
             c.telegram_relayMessagePhotoToOtherParticipants_By_File_ID(sender, tmfc);    
        }
        if(this.relayVoice && tmfc.u.hasMessage()&& tmfc.u.getMessage().hasVoice() ){
             c.telegram_relayMessageVoiceToOtherParticipants_By_File_ID(sender, tmfc);
        }
        if(tmfc.u.hasCallbackQuery()){
            CallbackQuery cbq = tmfc.u.getCallbackQuery();
            Message  m =cbq.getMessage();
            String callbackData =   cbq.getData();
            System.err.println("callbackdata: "+callbackData);
       
        }
        
        
        
    }
     //has to be organized from longest to shortest
     //"what about ha"
   
    
    HahaVariantGenerator hvg = new HahaVariantGenerator();
    
    
    
    public void processMessageFromClient(TelegramParticipant sender, TelegramMessageFromClient tmfc){
   
        if (!this.experimentalParticipants.contains(sender)){
             c.telegram_relayMessageTextToOtherParticipants(sender, tmfc);
             
             return;
        }
        
        
        try{
          String textFromSender = tmfc.u.getMessage().getText();
          
          String newText = hvg.filterOutHaha(textFromSender);
          if(newText==null ){
              c.telegram_relayMessageTextToOtherParticipants(sender, tmfc);
          }
          else if(newText.equalsIgnoreCase("")||newText.equalsIgnoreCase("\n")){
              //Don`t send anything
               Conversation.printWSln("Main", "Found standalone haha");
          }
          
          
          else {
              Conversation.printWSln("Main", "Found haha in turn");
              c.telegram_sendArtificialTurnFromApparentOriginToPermittedParticipants(sender, newText);
          }
                   
                
          
        }catch(Exception e){
            e.printStackTrace();
            Conversation.saveErr(e);
           
            c.telegram_relayMessageTextToOtherParticipants(sender, tmfc);
             Conversation.printWSln("Main", "Error processing turn "+tmfc.u.getMessage().getText());
        }
          
          
          
    }

    @Override
    public Vector<AttribVal> getAdditionalInformationForParticipant(Participant p) {
         CustomizableReferentialTask crt = (CustomizableReferentialTask)this.htCRT.get(p);
         if (crt==null) return new Vector();
         return crt.getAdditionalValues(p);

        
        
        
        //return super.getAdditionalInformationForParticipant(p); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
   
    
    
    
    
    
    
   public static boolean showcCONGUI() {
        return true;
    }
    
}
