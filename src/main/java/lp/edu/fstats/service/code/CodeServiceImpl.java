package lp.edu.fstats.service.code;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.code.CodeRequest;
import lp.edu.fstats.dto.code.CodeResponse;
import lp.edu.fstats.dto.code.CodesResponse;
import lp.edu.fstats.exception.custom.CustomDuplicateFieldException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.repository.code.CodeRepository;
import lp.edu.fstats.util.AuthUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {


    private final CodeRepository codeRepository;

    @Cacheable(value = "codes")
    @Override
    public CodesResponse getAllCodes() {
        List<Code> codes = codeRepository.findAll();

        if(!AuthUtil.hasElevatedPrivileges()){
            codes = codes.stream()
                    .filter(Code::isActive)
                    .toList();
        }

        return CodesResponse.toResponse(codes);
    }

    @CacheEvict(value = "codes", allEntries = true)
    @Override
    public CodeResponse createCode(CodeRequest request) {
        Code code = request.toModel();

        boolean hasConflict = codeRepository.existsByCode(request.code());

        if(hasConflict) {
            throw CustomDuplicateFieldException.code();
        }

        return new CodeResponse(codeRepository.save(code));
    }

    @CacheEvict(value = "codes", allEntries = true)
    @Override
    public void deleteCode(Integer codeId) {

        Code code = codeRepository.findById(codeId)
                .orElseThrow(CustomNotFoundException::competition);

        code.setActive(false);

        codeRepository.save(code);
    }

    @CacheEvict(value = "codes", allEntries = true)
    @Override
    public CodeResponse reactiveCode(Integer codeId){
        Code code = codeRepository.findById(codeId)
                .orElseThrow(CustomNotFoundException::competition);

        code.setActive(true);

        return new CodeResponse(codeRepository.save(code));
    }
}
