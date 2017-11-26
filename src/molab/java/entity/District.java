package molab.java.entity;

public class District {

	// Fields

	private Integer id;
	private Integer code;
	private String name;
	private String short_;

	// Constructors

	/** default constructor */
	public District() {
	}

	/** minimal constructor */
	public District(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	/** normal constructor */
	public District(Integer code, String name, String short_) {
		this.code = code;
		this.name = name;
		this.short_ = short_;
	}
	
	/** full constructor */
	public District(Integer id, Integer code, String name, String short_) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.short_ = short_;
	}

	// Property accessors
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCode() {
		return this.code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShort_() {
		return this.short_;
	}

	public void setShort_(String short_) {
		this.short_ = short_;
	}

}