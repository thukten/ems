package bt.gov.ditt.ems;

public class RegulationManagement {

	private static RegulationManagement regulation = null;
	
	public static RegulationManagement getInstance() {
		if(regulation == null)
			regulation = new RegulationManagement();
		return regulation;
	}
	
	
}
