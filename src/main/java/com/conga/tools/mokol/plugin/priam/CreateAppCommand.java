package com.conga.tools.mokol.plugin.priam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.conga.tools.mokol.CommandContext;
import com.conga.tools.mokol.ShellException;
import com.conga.tools.mokol.spi.annotation.Help;

/**
 * @author jflexa
 * 
 */
@Help("Create a Priam application by inserting properties on SimpleDB domain priamProperties")
public class CreateAppCommand extends PriamCommand {
	String[] names = new String[] { "priam.backup.threads",
			"priam.backup.hour", "priam.backup.incremental.enable",
			"priam.backup.chunksizemb", "priam.restore.prefix",
			"priam.restore.snapshot", "priam.s3.bucket", "priam.s3.base_dir",
			"priam.restore.threads", "priam.restore.closesttoken",
			"priam.restore.keyspaces", "priam.upload.throttle",
			"priam.cass.home", "priam.cass.startscript",
			"priam.cass.stopscript", "priam.data.location",
			"priam.cache.location", "priam.commitlog.location",
			"priam.clustername", "priam.endpoint_snitch",
			"priam.multiregion.enable", "priam.zones.available",
			"priam.memory.compaction.limit", "priam.compaction.throughput",
			"priam.heap.size", "priam.heap.newgen.size",
			"priam.direct.memory.size", "priam.storage.port",
			"priam.thrift.port", "priam.jmx.port" };
	List<String> propertyNames = Arrays.asList(names);

	@Override
	protected void execute(CommandContext context, List<String> args)
			throws ShellException {
		auth();
		context.printf("Creating Application\n");
		String appId = context.readLine("Provide application name: ");
		String region = context.readLine("Provide region name: ");
		String propertyName = null;
		while ((propertyName = getPropertyName(context)) != null
				&& propertyName.trim().length() > 0) {
			String propertyValue = context.readLine("Provide property value: ");
			sdb.batchPutAttributes(new BatchPutAttributesRequest(
					priamProperties, createSampleData(appId, region,
							propertyName, propertyValue)));
		}
	}

	private String getPropertyName(CommandContext context) {
		String pName = context.readLine("Provide property name: ");
		if (!propertyNames.contains(pName)) {
			context.printf("Property %s not known", pName);
		}
		return pName;
	}

	private List<ReplaceableItem> createSampleData(String appId,
			String propertyName, String propertyValue, String region) {
		List<ReplaceableItem> sampleData = new ArrayList<ReplaceableItem>();

		sampleData.add(new ReplaceableItem(appId + propertyName)
				.withAttributes(new ReplaceableAttribute("property",
						propertyName, true), new ReplaceableAttribute("value",
						propertyValue, true), new ReplaceableAttribute(
						"region", region, true), new ReplaceableAttribute(
						"appId", appId, true)));
		return sampleData;
	}
}
