package molab.java.entity;

public class Proxy {

	// Fields

	private Integer id;
	private String ip;
	private Integer port;
	private Integer districtCode;
	private String districtName;
	private Integer isused;
	private Long time;

	// Constructors

	/** default constructor */
	public Proxy() {
	}

	/** minimal constructor */
	public Proxy(String ip, Integer port, Integer isused, Long time) {
		this.ip = ip;
		this.port = port;
		this.isused = isused;
		this.time = time;
	}
	
	/** normal constructor */
	public Proxy(Integer id, String ip, Integer port, Integer isused, Long time) {
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.isused = isused;
		this.time = time;
	}
	
	/** full constructor */
	public Proxy(String ip, Integer port, Integer districtCode,
			String districtName, Integer isused, Long time) {
		this.ip = ip;
		this.port = port;
		this.districtCode = districtCode;
		this.districtName = districtName;
		this.isused = isused;
		this.time = time;
	}

	// Property accessors
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	public Integer getDistrictCode() {
		return this.districtCode;
	}

	public void setDistrictCode(Integer districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictName() {
		return this.districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	
	public Integer getIsused() {
		return this.isused;
	}

	public void setIsused(Integer isused) {
		this.isused = isused;
	}

	public Long getTime() {
		return this.time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

}