
import com.payline.payment.bizum.utils.Constants;
import com.payline.payment.bizum.utils.http.BizumHttpClient;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;


class MainTester {
    private static final Logger LOGGER = LogManager.getLogger(MainTester.class);
    private static final BizumHttpClient bizumHttpClient = BizumHttpClient.getInstance();
    /**------------------------------------------------------------------------------------------------------------------*/
    public static void main(String[] args) throws IOException {

        try {



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private static ContractConfiguration initContractConfiguration(){

        ContractConfiguration contractConfiguration = new ContractConfiguration("Bizum", new HashMap<>());
        // TODO: Configurer la clé dans la configuration projet
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.KEY, new ContractProperty( System.getProperty("project.Key")));

        return contractConfiguration;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
}
