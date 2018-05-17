package com.firelotto.stats.extapi

object EtherScanRPC {

    case class RPCRequest(command: String, address: String)

    sealed trait RPCResponse

    object RPCResponse

    /**
      * {
      * "status":"1",
      * "message":"OK",
      * "result": Array[TxList]
      * }
      */
    case class TxListResponse(status: String,
                              message: String,
                              result: Vector[TxList]) extends RPCResponse

    /**
      * "blockNumber":"4909780",
      * "timeStamp":"1515975539",
      * "hash":"0x457d4c3aacc55be3e5873dfce87f77a0d4602a6c95e77f9e0bb756dbcf3d7757",
      * "nonce":"32",
      * "blockHash":"0x6208fdd3653dd4609564fcca25bd2fd48476f938559f8293f7fc6c2a24eec80e",
      * "transactionIndex":"40",
      * "from":"0x9b90570aafbd9db71e85d68835c74bcfb7de91ba",
      * "to":"",
      * "value":"0",
      * "gas":"5794071",
      * "gasPrice":"7000000000",
      * "isError":"0",
      * "txreceipt_status":"1",
      * "input":"0x6",
      * "contractAddress":"0xa0306fcae88f84cbbe2cf784b1046a94def54015",
      * "cumulativeGasUsed":"7512421",
      * "gasUsed":"5794071",
      * "confirmations":"231122"
      */
    case class TxList(blockNumber: String,
                      timeStamp: String,
                      hash: String,
                      nonce: String,
                      blockHash: String,
                      transactionIndex: String,
                      from: String,
                      to: String,
                      value: String,
                      gas: String,
                      gasPrice: String,
                      isError: String,
                      txreceipt_status: String,
                      input: String,
                      contractAddress: String,
                      cumulativeGasUsed: String,
                      gasUsed: String,
                      confirmations: String)

}