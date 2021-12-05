package nodomain.freeyourgadget.gadgetbridge.entities;

public class ActivityDescTagLink {
    private long activityDescriptionId;

    /* renamed from: id */
    private Long f133id;
    private long tagId;

    public ActivityDescTagLink() {
    }

    public ActivityDescTagLink(Long id) {
        this.f133id = id;
    }

    public ActivityDescTagLink(Long id, long activityDescriptionId2, long tagId2) {
        this.f133id = id;
        this.activityDescriptionId = activityDescriptionId2;
        this.tagId = tagId2;
    }

    public Long getId() {
        return this.f133id;
    }

    public void setId(Long id) {
        this.f133id = id;
    }

    public long getActivityDescriptionId() {
        return this.activityDescriptionId;
    }

    public void setActivityDescriptionId(long activityDescriptionId2) {
        this.activityDescriptionId = activityDescriptionId2;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId2) {
        this.tagId = tagId2;
    }
}
