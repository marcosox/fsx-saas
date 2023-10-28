package io.github.marcosox.fsxsaas.fsx.models;

@SuppressWarnings("unused")
public class SimStatus {

	private boolean paused;
	private boolean simRunningStatus;
	private String flightFilePath;
	private String airFilePath;

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isSimRunningStatus() {
		return simRunningStatus;
	}

	public void setSimRunningStatus(boolean simRunningStatus) {
		this.simRunningStatus = simRunningStatus;
	}

	public String getFlightFilePath() {
		return flightFilePath;
	}

	public void setFlightFilePath(String flightFilePath) {
		this.flightFilePath = flightFilePath;
	}

	public String getAirFilePath() {
		return airFilePath;
	}

	public void setAirFilePath(String airFilePath) {
		this.airFilePath = airFilePath;
	}
}
