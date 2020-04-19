import mutations from "./mutations";

const initMetrics = {
  success_rate: 0,
  success_rate_rank: 0.0,
  count_queries: 0,
  count_responses: 0,
  count_fallbacks: 0,
  avg_latency: 0,
  avg_latency_unit: "ms",
  avg_latency_rank: 0.0,
  min_latency: 0,
  min_latency_unit: "ms",
  max_latency: 0,
  max_latency_unit: "ms",
  avg_throughput: 0,
  avg_throughput_unit: "s",
  avg_throughput_rank: 0.0,
  throughput_queries: 0,
  throughput_queries_unit: "s",
  throughput_responses: 0,
  throughput_responses_unit: "s",
};

test("metrics should be  updated", () => {
  let initState = { otherState: "bla", metrics: initMetrics };
  let newState = mutations.metrics(initState, {
    ...initMetrics,
    count_queries: 2,
  });

  expect(newState.otherState).toBe("bla");
  expect(newState.metrics.count_queries).toBe(2);
});
