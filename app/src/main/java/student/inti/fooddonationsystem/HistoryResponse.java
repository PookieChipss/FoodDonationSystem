package student.inti.fooddonationsystem;

import java.util.List;

public class HistoryResponse {
    private String status;
    private List<HistoryItem> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<HistoryItem> getData() {
        return data;
    }

    public void setData(List<HistoryItem> data) {
        this.data = data;
    }
}
