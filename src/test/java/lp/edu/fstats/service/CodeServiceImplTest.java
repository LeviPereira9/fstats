package lp.edu.fstats.service;
import lp.edu.fstats.dto.code.CodeRequest;
import lp.edu.fstats.dto.code.CodeResponse;
import lp.edu.fstats.dto.code.CodesResponse;
import lp.edu.fstats.exception.custom.CustomDuplicateFieldException;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.repository.code.CodeRepository;
import lp.edu.fstats.service.code.CodeServiceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CodeServiceImplTest {

    @Mock
    private CodeRepository codeRepository;

    @InjectMocks
    private CodeServiceImpl codeService;

    //Helpers
    private Code buildCode(Integer id, String code, String name){
        Code c = new Code();
        c.setId(id);
        c.setCode(code);
        c.setName(name);

        return c;
    }

    //getAllCodes

    @Test
    void getAllCodes_shouldReturnAllCodes_whenCodesExist(){
        List<Code> codes = List.of(
                this.buildCode(1,"PL", "Premier League"),
                this.buildCode(2, "BL1", "BundesLiga")
        );

        when(codeRepository.findAll()).thenReturn(codes);

        CodesResponse response = codeService.getAllCodes();

        assertNotNull(response);
        assertEquals(2, response.codes().size());
        assertEquals("PL", response.codes().get(0).code());
        assertEquals("BL1", response.codes().get(1).code());

        verify(codeRepository).findAll();
    }

    // createCode
    @Test
    void createCode_shouldReturnCodeResponse_whenCodeDoesNotExist(){
        CodeRequest request = new CodeRequest("PL", "Premier League");
        Code savedCode = this.buildCode(1, "PL", null);

        when(codeRepository.existsByCode("PL")).thenReturn(false);

        when(codeRepository.save(any(Code.class))).thenReturn(savedCode);

        CodeResponse response = codeService.createCode(request);

        assertNotNull(response);
        assertEquals("PL", response.code());

        verify(codeRepository).save(any(Code.class));
    }

    @Test
    void createCode_shouldThrowDuplicateField_whenCodeAlreadyExists(){
        CodeRequest request = new CodeRequest("PL", "Premier League");

        when(codeRepository.existsByCode("PL")).thenReturn(true);

        assertThrows(CustomDuplicateFieldException.class,
                ()-> codeService.createCode(request));

        verify(codeRepository, never()).save(any());
    }

    // deleteCode

    @Test
    void deleteCode_shouldCallDeleteById_whenCodeExists (){
        codeService.deleteCode(1);

        verify(codeRepository).deleteById(1);
    }
}
