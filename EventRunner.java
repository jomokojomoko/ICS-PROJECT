import java.io.*;
public class EventRunner{
	private Player player; 
	private Event [] events; 
	private Ending [] endings; 
	int eventNum;  
	
	/* Reference for ids of the stats
	 * 0 - no stat req
	 * 1 - linguistic intelligence
	 * 2 - spatial intelligence
	 * 3 - logical intelligence
	 * 4 - expression charisma
	 * 5 - social charisma 
	 * 6 - luck
 	 * 7 - happiness
	 *	8 - strength
	 */
	 
	 
	/* Number for each event type
	 * 1 - Routine event
	 * 2 - Random event (reusable)
	 * 3 - Predetermined event (tracks time)
	 */
	 
	public EventRunner(Player p, File eventFile, File endingsFile){
		try {
			player = p;
			
			BufferedReader in = new BufferedReader(new FileReader(eventFile)); //reads in event information
			BufferedReader in2 = new BufferedReader(new FileReader(endingsFile)); //reads in endings information 
			int eventType; 
			int numPhases; 
			int numChoices; 
			int statReqType; //type of stat required
			int statReq; //minimum of that stat needed
			String id; 
			int changeType; 
			int changeAmount; 
			
			String title;
			int numEndings; 
			int luckReq, hapReq, stat1, req1, stat2, req2, numLinesOfText;
			String text = ""; 
			
			String flush; //gets rid of empty lines in the file
			 
			numEndings = Integer.parseInt(in2.readLine()); 
			for(int i = 0; i < numEndings; i++){
				title = in2.readLine();
				luckReq = Integer.parseInt(in2.readLine());
				hapReq = Integer.parseInt(in2.readLine());
				stat1 = Integer.parseInt(in2.readLine());
				req1 = Integer.parseInt(in2.readLine());
				stat2 = Integer.parseInt(in2.readLine());
				req2 = Integer.parseInt(in2.readLine());

				for (int j = 0; j < 3; j++){
					text = in.readLine() + "\n";
				}
				flush = in.readLine();
				endings[i] = new Ending (title, luckReq, hapReq, stat1, req1, stat2, req2, text);
			}
			
			EventPhase [] phases;
			String [] phaseText; //base text that makes up each phase
			Choice [] choices; 
						
			eventNum = Integer.parseInt(in.readLine()); // number of events
			events = new Event[eventNum];
			for (int i = 0; i < eventNum; i++){
				eventType = Integer.parseInt(in.readLine()); //reads in type of event
				id = in.readLine(); 
				statReqType = Integer.parseInt(in.readLine()); //type of stat required
				statReq = Integer.parseInt(in.readLine()); //minimum stat number that needs to be met
			
				numPhases = Integer.parseInt(in.readLine()); //reads in number of phases in an event		
				phases = new EventPhase [numPhases]; //creates array of phases
				phaseText = new String [numPhases];
				for (int j = 0; j < numPhases; j++){
					phaseText[j] = in.readLine(); 	
					numChoices = Integer.parseInt(in.readLine()); //reads in number of choices of a phase
				    choices = new Choice [numChoices]; //creates array of choices for a phase
					
					for (int k = 0; k < numChoices; k++){
						choices[k] = new Choice(k + 1, in.readLine(), in.readLine());
						//first reads in the text of the choice
						//right below it should be its change to the story if you pick it 
					}
					phases[j] = new EventPhase(choices); 
				}
				changeType = Integer.parseInt(in.readLine());
				changeAmount = Integer.parseInt(in.readLine());
				if (eventType == 1){
					events [i] = new Routine(id, statReqType, statReq, changeType, changeAmount, phases);
				} else if (eventType == 2){ // recyclable 
					events [i] = new Random(id, statReqType, statReq, changeType, changeAmount, phases);
				} else if (eventType == 3){ //keeps track of time
					int month = Integer.parseInt(in.readLine());
					events [i] = new Predetermined(id, statReqType, statReq, changeType, changeAmount, month, phases);
				}
				flush = in.readLine(); //gets rid of the empty space between events 
			}
		} catch (IOException e){
		}
	}
	
	public void updatePlayer(Player p){
		player = p;
	}
	
	private Event evaluateStatsForEvents(int eventType, int month){
		boolean foundEvent = false;
		Event pE = events[0]; // Potential event 
		for (int h = 0; h < 2; h++){
			for (int i = 0; !foundEvent && i < events.length; i++){ 
				switch (events[i].getStatType()){
					case 0: 
						 pE = events[i]; 
						 break;
					case 1:
					 	if (getAStat(1) >= events[i].getStatReq()){
					 		pE = events[i]; 
					 	}
					 	break;
					 	
					case 2: 
						if (getAStat(2) >= events[i].getStatReq()){
					 		pE = events[i]; 
					 	}
						break; 
					
					case 3:
					 	if (getAStat(3) >= events[i].getStatReq()){
					 		pE = events[i]; 
					 	}
					 	break;
					
					case 4: 
						if (getAStat(4) >= events[i].getStatReq()){
					 		pE = events[i]; 
					 	}
						break; 
					
					case 5:
					 	if (getAStat(5) >= events[i].getStatReq()){
					 		pE = events[i]; 
					 	}
					 	break;
					
					case 6: 
						if (getAStat(6) >= events[i].getStatReq()){
					 		pE = events[i]; 
					 	}
						break; 
					
					case 7:
					 	if (getAStat(7) >= events[i].getStatReq()){
					 		pE = events[i]; 
					 	}
				   break;
					
					case 8: 
						if (getAStat(8) >= events[i].getStatReq()){
					 		pE = events[i]; 
					 	}
						break; 
				} 
				
				if (eventType == 1) {
					if (pE instanceof Routine && !pE.getOccured()){
						return pE; 
					}
				} else if (eventType == 2){
					if (pE instanceof Random){
						return pE; 
					}
				} else if (eventType == 3){
					if (pE instanceof Random && !pE.getOccured()){
						Predetermined temp = (Predetermined) pE; 
						if (temp.getMonthReq() == month){
							return pE; 
						}
					}
				} else {
					return null;
				}
			
			} // end of for loop
			eventType = 2; // if an event of type 1 or 3 was needed but all have occured, 
			// sets the search parameter to type 2 which are the recyclable events
		}
		return null;
	}	
	
	public void rollEvent(int month){
		//updatePlayer(p); //updates player stats for any possible adjustments since last playing period
		Event e1, e2, e3;
		e1 = evaluateStatsForEvents(1, month); 
		e2 = evaluateStatsForEvents(2, month);
		e3 = evaluateStatsForEvents(3, month);
		e1.play();
		setAStat(e1.getChangeType(), e1.getChangeAmount()); 
		e2.play();
		setAStat(e2.getChangeType(), e2.getChangeAmount()); 
		e3.play();
		setAStat(e3.getChangeType(), e3.getChangeAmount()); 
	}
	
	public void rollEnding(){
		for (int i = 0; i < endings.length; i++){
			if (getAStat(7) >= endings[i].getHapReq() && getAStat(6) >= endings[i].getLuckReq()){
				if (getAStat(endings[i].getStat1()) >= endings[i].getStat1Req() && getAStat(endings[i].getStat2()) >= endings[i].getStat2Req()){
					System.out.println("Ending: " + endings[i].getTitle());
					System.out.println(endings[i].getText());
				}
			}
		}
	}
	
	public int getAStat(int statType){
		switch (statType){ 				
			case 1:
				return player.getStats().getLinguisticIntelligence();
			case 2: 
				return player.getStats().getSpatialIntelligence();
			case 3:
				return player.getStats().getLogicalIntelligence();
			case 4: 
				return player.getStats().getExpressionCharisma();
			case 5:
				return player.getStats().getSocialCharisma();
			case 6: 
				return player.getStats().getLuck();
			case 7:
				return player.getStats().getHappiness();
			case 8: 
				return player.getStats().getStrength();
		}
		return -1; 
	}
	
	public void setAStat(int statType, int addBy){
		switch (statType){ 				
			case 1:
				player.getStats().setLinguisticIntelligence(getAStat(statType) + addBy);
			 	break;
			case 2: 
				player.getStats().setSpatialIntelligence(getAStat(statType) + addBy);
				break; 
			case 3:
				player.getStats().setLogicalIntelligence(getAStat(statType) + addBy);
				break;	
			case 4:
				player.getStats().setExpressionCharisma(getAStat(statType) + addBy);
				break; 
			case 5:
				player.getStats().setSocialCharisma(getAStat(statType) + addBy);
				break;
			case 6: 
				player.getStats().setLuck(getAStat(statType) + addBy);
				break;
			case 7:
				player.getStats().setHappiness(getAStat(statType) + addBy);
				break;
			case 8: 
				player.getStats().setStrength(getAStat(statType) + addBy);
				break;
		}
	}
}