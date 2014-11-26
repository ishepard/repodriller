package br.com.metricminer2.study.selenium;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Modification;
import br.com.metricminer2.metric.MetricCalculator;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.SCMRepository;

public class AddsAndRemoves implements MetricCalculator {

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {

		for(Modification m : commit.getModifications()) {
			if(Utils.isASeleniumTest(m)) {
				String[] lines = m.getDiff().replace("\r", "").split("\n");
				
				for(String line : lines) {
					for(Categories c : Categories.values()) {
						if(isAddOrRemove(line) && c.isContainedIn(line)) {
							writer.write(
								repo.getLastDir(), 
								commit.getHash(), 
								Utils.format(commit.getDate()), 
								m.getNewPath(), 
								c.name(), 
								line.startsWith("+") ? "added" : "removed"
							);
						}
					}
				}
			}
		}
	}
	
	private boolean isAddOrRemove(String line) {
		return line.startsWith("+") || line.startsWith("-");
	}
	
	@Override
	public String name() {
		return "selenium adds and removes";
	}

}
