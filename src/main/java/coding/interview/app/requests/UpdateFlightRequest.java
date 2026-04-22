package coding.interview.app.requests;

public record UpdateFlightRequest(
        String code,
        String origin,
        String destination,
        String status
) {
}
