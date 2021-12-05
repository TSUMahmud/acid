package nodomain.freeyourgadget.gadgetbridge.entities;

import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.model.ValidByDate;

public class UserAttributes implements ValidByDate {
    private int heightCM;

    /* renamed from: id */
    private Long f152id;
    private Integer sleepGoalHPD;
    private Integer stepsGoalSPD;
    private long userId;
    private Date validFromUTC;
    private Date validToUTC;
    private int weightKG;

    public UserAttributes() {
    }

    public UserAttributes(Long id) {
        this.f152id = id;
    }

    public UserAttributes(Long id, int heightCM2, int weightKG2, Integer sleepGoalHPD2, Integer stepsGoalSPD2, Date validFromUTC2, Date validToUTC2, long userId2) {
        this.f152id = id;
        this.heightCM = heightCM2;
        this.weightKG = weightKG2;
        this.sleepGoalHPD = sleepGoalHPD2;
        this.stepsGoalSPD = stepsGoalSPD2;
        this.validFromUTC = validFromUTC2;
        this.validToUTC = validToUTC2;
        this.userId = userId2;
    }

    public Long getId() {
        return this.f152id;
    }

    public void setId(Long id) {
        this.f152id = id;
    }

    public int getHeightCM() {
        return this.heightCM;
    }

    public void setHeightCM(int heightCM2) {
        this.heightCM = heightCM2;
    }

    public int getWeightKG() {
        return this.weightKG;
    }

    public void setWeightKG(int weightKG2) {
        this.weightKG = weightKG2;
    }

    public Integer getSleepGoalHPD() {
        return this.sleepGoalHPD;
    }

    public void setSleepGoalHPD(Integer sleepGoalHPD2) {
        this.sleepGoalHPD = sleepGoalHPD2;
    }

    public Integer getStepsGoalSPD() {
        return this.stepsGoalSPD;
    }

    public void setStepsGoalSPD(Integer stepsGoalSPD2) {
        this.stepsGoalSPD = stepsGoalSPD2;
    }

    public Date getValidFromUTC() {
        return this.validFromUTC;
    }

    public void setValidFromUTC(Date validFromUTC2) {
        this.validFromUTC = validFromUTC2;
    }

    public Date getValidToUTC() {
        return this.validToUTC;
    }

    public void setValidToUTC(Date validToUTC2) {
        this.validToUTC = validToUTC2;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId2) {
        this.userId = userId2;
    }
}
