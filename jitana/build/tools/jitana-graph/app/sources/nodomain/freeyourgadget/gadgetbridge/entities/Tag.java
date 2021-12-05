package nodomain.freeyourgadget.gadgetbridge.entities;

public class Tag {
    private String description;

    /* renamed from: id */
    private Long f149id;
    private String name;
    private long userId;

    public Tag() {
    }

    public Tag(Long id) {
        this.f149id = id;
    }

    public Tag(Long id, String name2, String description2, long userId2) {
        this.f149id = id;
        this.name = name2;
        this.description = description2;
        this.userId = userId2;
    }

    public Long getId() {
        return this.f149id;
    }

    public void setId(Long id) {
        this.f149id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description2) {
        this.description = description2;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId2) {
        this.userId = userId2;
    }
}
