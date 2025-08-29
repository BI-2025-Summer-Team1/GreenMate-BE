package kr.bi.greenmate.service;

import java.util.List;

public record ImagesToDeleteEvent(List<String> keys) {
}
