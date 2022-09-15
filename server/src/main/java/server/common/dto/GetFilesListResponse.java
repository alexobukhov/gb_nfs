package server.common.dto;

import java.util.List;

public class GetFilesListResponse extends BasicResponse {

    private final List<String> list;

    public List<String> getList() {
        return list;
    }

    public GetFilesListResponse(String errorMessage, List<String> list) {
        super(errorMessage);
        this.list = list;
    }
}
