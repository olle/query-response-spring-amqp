<template>
  <article id="overview">
    <h1>Query/Response System Overview</h1>

    <section class="overview">
      <!-- TODO: This needs fragments, to make sense. Looking at vue-fragment. -->
      <h2>Success Rate</h2>
      <data class="big one">94%</data>
      <h3>Queries</h3>
      <data>3.83M</data>
      <h3>Responses</h3>
      <data>3.11M</data>
      <h3>Fallback</h3>
      <data>72K</data>

      <h2>Latency (Avg)</h2>
      <time class="big two">32ms</time>
      <h3>Min</h3>
      <time>4ms</time>
      <h3>Max</h3>
      <time>332ms</time>

      <h2>Throughput (Avg)</h2>
      <data class="big three">32K/s</data>
      <h3>Queries</h3>
      <data>2K/s</data>
      <h3>Responses</h3>
      <data>30k/s</data>
    </section>

    <table class="nodes">
      <caption class="h2">
        Live Nodes
      </caption>
      <thead>
        <tr>
          <th class="h3">Name</th>
          <th class="h3">Index</th>
          <th class="h3">Address</th>
          <th class="h3">Uptime</th>
          <th class="h3">Success Rate</th>
          <th class="h3">Latency (Avg)</th>
          <th class="h3">Throughput (Avg)</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>jembo</td>
          <td>0</td>
          <td>lb1-jembo.some.cloud</td>
          <td><time>3 hours</time></td>
          <td>
            <data class="warning">12%</data>
            <small>
              <data data-abbr="Q" title="Queries">232K</data>
              <data data-abbr="R" title="Responses">13K</data>
              <data data-abbr="F" title="Fallback">13K</data>
            </small>
          </td>
          <td>
            <time>44ms</time>
            <small>
              <time data-abbr="Min" title="Min">2ms</time>
              <time data-abbr="Max" title="Max">231ms</time>
            </small>
          </td>
          <td>
            <data>55K/s</data>
            <small>
              <data data-abbr="Q" title="Queries">2K/s</data>
              <data data-abbr="R" title="Responses">312K/s</data>
            </small>
          </td>
        </tr>
        <tr>
          <td>jembo</td>
          <td>1</td>
          <td>lb2-jembo.some.cloud</td>
          <td><time>3 hours</time></td>
          <td>
            <data>55%</data>
            <small>
              <data data-abbr="Q" title="Queries">232K</data>
              <data data-abbr="R" title="Responses">13K</data>
              <data data-abbr="F" title="Fallback">13K</data>
            </small>
          </td>
          <td>
            <time>24ms</time>
            <small>
              <time data-abbr="Min" title="Min">2ms</time>
              <time data-abbr="Max" title="Max">231ms</time>
            </small>
          </td>
          <td>
            <data>41K/s</data>
            <small>
              <data data-abbr="Q" title="Queries">2K/s</data>
              <data data-abbr="R" title="Responses">312K/s</data>
            </small>
          </td>
        </tr>
        <tr>
          <td>jembo</td>
          <td>2</td>
          <td>lb3-jembo.some.cloud</td>
          <td><time>2 hours</time></td>
          <td>
            <data>55%</data>
            <small>
              <data data-abbr="Q" title="Queries">232K</data>
              <data data-abbr="R" title="Responses">13K</data>
              <data data-abbr="F" title="Fallback">13K</data>
            </small>
          </td>
          <td>
            <time>24ms</time>
            <small>
              <time data-abbr="Min" title="Min">2ms</time>
              <time data-abbr="Max" title="Max">231ms</time>
            </small>
          </td>
          <td>
            <data>41K/s</data>
            <small>
              <data data-abbr="Q" title="Queries">2K/s</data>
              <data data-abbr="R" title="Responses">312K/s</data>
            </small>
          </td>
        </tr>
        <tr>
          <td>profile-service</td>
          <td>0</td>
          <td>profiles0.some.cloud</td>
          <td><time>53 days</time></td>
          <td>
            <data>97%</data>
            <small>
              <data data-abbr="Q" title="Queries">3k</data>
              <data data-abbr="R" title="Responses">1.332M</data>
              <data data-abbr="F" title="Fallback">543</data>
            </small>
          </td>
          <td>
            <time>24ms</time>
            <small>
              <time data-abbr="Min" title="Min">2ms</time>
              <time data-abbr="Max" title="Max">231ms</time>
            </small>
          </td>
          <td>
            <data>41K/s</data>
            <small>
              <data data-abbr="Q" title="Queries">2K/s</data>
              <data data-abbr="R" title="Responses">312K/s</data>
            </small>
          </td>
        </tr>
        <tr>
          <td>profiles</td>
          <td>0</td>
          <td>profiles1.some.cloud</td>
          <td><time>123 days</time></td>
          <td>
            <data class="failure">1%</data>
            <small>
              <data data-abbr="Q" title="Queries">14</data>
              <data data-abbr="R" title="Responses">0</data>
              <data data-abbr="F" title="Fallback">0</data>
            </small>
          </td>
          <td>
            <time>24ms</time>
            <small>
              <time data-abbr="Min" title="Min">2ms</time>
              <time data-abbr="Max" title="Max">231ms</time>
            </small>
          </td>
          <td>
            <data>41K/s</data>
            <small>
              <data data-abbr="Q" title="Queries">2K/s</data>
              <data data-abbr="R" title="Responses">312K/s</data>
            </small>
          </td>
        </tr>
      </tbody>
    </table>
  </article>
</template>

<script>
export default {
  name: "Overview"
};
</script>
