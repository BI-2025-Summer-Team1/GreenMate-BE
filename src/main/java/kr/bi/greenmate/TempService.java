package kr.bi.greenmate;

import kr.bi.greenmate.repository.ObjectStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TempService {
    @Autowired
    private ObjectStorageRepository objectStorageRepository;

    String temp(){
        objectStorageRepository.delete("test/test.txt");
        return objectStorageRepository.getDownloadUrl("test/test.txt");
    }
}
