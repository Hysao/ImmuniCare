package com.myprograms.immunicare.healthworker.update;

import java.util.List;

public class History {
    private String hWorkerId;
    private String childId;
    private List<String> changes;
    private long timestamp;
    private String historyDocumentId;
    private String childName;
    private String hWorkerName;


    public History() {}


    public History(String hWorkerId, String childId, List<String> changes, long timestamp, String historyDocumentId, String childName, String hWorkerName) {
        this.hWorkerId = hWorkerId;
        this.childId = childId;
        this.changes = changes;
        this.timestamp = timestamp;
        this.historyDocumentId = historyDocumentId;
        this.childName = childName;
        this.hWorkerName = hWorkerName;
    }


    public String gethWorkerId() { return hWorkerId; }
    public void sethWorkerId(String hWorkerId) { this.hWorkerId = hWorkerId; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public List<String> getChanges() { return changes; }
    public void setChanges(List<String> changes) { this.changes = changes; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getHistoryDocumentId() { return historyDocumentId; }
    public void setHistoryDocumentId(String historyDocumentId) { this.historyDocumentId = historyDocumentId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public String gethWorkerName() { return hWorkerName; }
    public void sethWorkerName(String hWorkerName) { this.hWorkerName = hWorkerName; }




    public String getUpdatedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }


    public String getUpdatedCheckboxes() {
        return String.join("\n", changes);
    }
}
