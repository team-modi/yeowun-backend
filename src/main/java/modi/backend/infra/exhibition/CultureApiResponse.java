package modi.backend.infra.exhibition;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 한눈에보는문화정보(15138937) realm 응답 매핑. (공공데이터 리뷰 4.A 응답 스키마)
 * 결측·미지의 필드가 잦은 원천이라 {@code ignoreUnknown=true}로 관대하게 파싱한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CultureApiResponse(Response response) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Response(ComMsgHeader comMsgHeader, MsgBody msgBody) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ComMsgHeader(String responseTime, String successYN, String returnCode, String errMsg) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record MsgBody(List<PerforItem> perforList) {
	}

	/** 전시 이벤트 한 건. 날짜는 YYYYMMDD 문자열, 좌표·썸네일은 결측 가능. */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record PerforItem(
			String seq,
			String title,
			String startDate,
			String endDate,
			String place,
			String realmName,
			String area,
			String thumbnail,
			String gpsX,
			String gpsY,
			String serviceName,
			String url) {
	}

	/** returnCode "00" + successYN "Y"만 정상으로 본다. */
	public boolean isSuccess() {
		return response != null && response.comMsgHeader() != null
				&& "Y".equalsIgnoreCase(response.comMsgHeader().successYN())
				&& "00".equals(response.comMsgHeader().returnCode());
	}

	/** 결과 목록(없으면 빈 목록). */
	public List<PerforItem> items() {
		if (response == null || response.msgBody() == null || response.msgBody().perforList() == null) {
			return List.of();
		}
		return response.msgBody().perforList();
	}
}
