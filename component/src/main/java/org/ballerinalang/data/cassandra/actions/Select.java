/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.data.cassandra.actions;

import org.ballerinalang.bre.Context;
import org.ballerinalang.data.cassandra.CassandraDataSource;
import org.ballerinalang.data.cassandra.CassandraDataSourceUtils;
import org.ballerinalang.data.cassandra.Constants;
import org.ballerinalang.model.types.BStructType;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BTable;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
/**
 * {@code Select} action executes a given query and returns a datatable.
 *
 * @since 0.95.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "data.cassandra",
        functionName = "select",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "ClientConnector"),
        args = {@Argument(name = "queryString", type = TypeKind.STRING),
                @Argument(name = "parameters", type = TypeKind.ARRAY, elementType = TypeKind.STRUCT,
                          structType = "Parameter")
        },
        returnType = { @ReturnType(type = TypeKind.TABLE) }
)
public class Select extends AbstractCassandraAction {

    @Override
    public void execute(Context context) {
        BStruct bConnector = (BStruct) context.getRefArgument(0);
        String query = context.getStringArgument(0);
        BRefValueArray parameters = (BRefValueArray) context.getRefArgument(1);
        BStructType structType = getStructType(context);
        CassandraDataSource dataSource = (CassandraDataSource) bConnector.getNativeData(Constants.CLIENT_CONNECTOR);
        try {
            BTable dataTable = executeSelect(dataSource, query, parameters, structType);
            context.setReturnValues(dataTable);
        } catch (Throwable e) {
            context.setReturnValues(CassandraDataSourceUtils.getCassandraConnectorError(context, e));
        }
    }
}
