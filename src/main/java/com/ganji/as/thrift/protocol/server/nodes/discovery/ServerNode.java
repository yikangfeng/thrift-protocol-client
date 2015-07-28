/**
 * 
 */
package com.ganji.as.thrift.protocol.server.nodes.discovery;

/**
 * @author yikangfeng
 * @date   2015年7月21日 
 */
public interface ServerNode {
    String getHost();
    int getPort();
    int getWeight();
}
